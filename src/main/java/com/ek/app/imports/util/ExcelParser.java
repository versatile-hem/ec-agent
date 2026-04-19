package com.ek.app.imports.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.ek.app.imports.dto.ImportRowDTO;

import org.springframework.http.HttpStatus;

@Component
public class ExcelParser {

    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    public List<ImportRowDTO> parse(MultipartFile file) {
        validateFile(file);

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Excel header row is missing");
            }

            Map<String, Integer> headerMap = buildHeaderMap(headerRow);
            List<ImportRowDTO> rows = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }

                String customerName = readString(row, headerMap, "customername");
                String productName = readString(row, headerMap, "productname");
                String sku = readString(row, headerMap, "sku");
                BigDecimal quantity = readBigDecimal(row, headerMap, "quantity");
                BigDecimal totalPrice = readBigDecimal(row, headerMap, "totalprice");
                LocalDate orderDate = readDate(row, headerMap, "orderdate");
                String channel = readString(row, headerMap, "channel");

                rows.add(ImportRowDTO.builder()
                        .rowNumber(i + 1)
                        .customerName(customerName)
                        .productName(productName)
                        .sku(sku)
                        .quantity(quantity)
                        .totalPrice(totalPrice)
                        .orderDate(orderDate)
                        .channel(channel)
                        .build());
            }
            return rows;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse excel: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file is required");
        }
        String name = file.getOriginalFilename();
        if (name == null || (!name.toLowerCase(Locale.ROOT).endsWith(".xlsx") && !name.toLowerCase(Locale.ROOT).endsWith(".xls"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only Excel files are supported (.xlsx/.xls)");
        }
    }

    private Map<String, Integer> buildHeaderMap(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        DataFormatter formatter = new DataFormatter();

        for (Cell cell : headerRow) {
            String key = normalizeHeader(formatter.formatCellValue(cell));
            if (!key.isBlank()) {
                map.put(key, cell.getColumnIndex());
            }
        }

        if (!map.containsKey("customername")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing header: Customer Name");
        }
        if (!map.containsKey("quantity")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing header: Quantity");
        }
        if (!map.containsKey("totalprice")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing header: Total Price");
        }
        if (!map.containsKey("orderdate")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing header: Order Date");
        }
        if (!map.containsKey("sku") && !map.containsKey("productname")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Either SKU or Product Name header is required");
        }

        return map;
    }

    private String normalizeHeader(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT)
                .replace("_", "")
                .replace(" ", "")
                .replace("-", "");
    }

    private String readString(Row row, Map<String, Integer> headers, String key) {
        Integer idx = headers.get(key);
        if (idx == null) {
            return null;
        }
        Cell cell = row.getCell(idx);
        if (cell == null) {
            return null;
        }
        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);
        return value == null ? null : value.trim();
    }

    private BigDecimal readBigDecimal(Row row, Map<String, Integer> headers, String key) {
        String value = readString(row, headers, key);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(value.replace(",", "").trim());
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid number at row " + (row.getRowNum() + 1) + " for " + key);
        }
    }

    private LocalDate readDate(Row row, Map<String, Integer> headers, String key) {
        Integer idx = headers.get(key);
        if (idx == null) {
            return null;
        }
        Cell cell = row.getCell(idx);
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return LocalDateTime.ofInstant(cell.getDateCellValue().toInstant(), ZoneId.systemDefault()).toLocalDate();
        }

        String value = readString(row, headers, key);
        if (value == null || value.isBlank()) {
            return null;
        }

        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(value.trim(), formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid date at row " + (row.getRowNum() + 1) + " for " + key + ". Use yyyy-MM-dd");
    }

    private boolean isEmptyRow(Row row) {
        DataFormatter formatter = new DataFormatter();
        for (Cell cell : row) {
            if (!formatter.formatCellValue(cell).isBlank()) {
                return false;
            }
        }
        return true;
    }
}
