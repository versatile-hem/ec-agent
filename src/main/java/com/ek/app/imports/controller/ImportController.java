package com.ek.app.imports.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ek.app.imports.dto.ImportSummaryResponse;
import com.ek.app.imports.service.ExcelImportService;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final ExcelImportService excelImportService;

    public ImportController(ExcelImportService excelImportService) {
        this.excelImportService = excelImportService;
    }

    @PostMapping(value = "/sales-orders", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportSummaryResponse importSalesOrders(@RequestParam("file") MultipartFile file) {
        return excelImportService.importSalesOrders(file);
    }
}
