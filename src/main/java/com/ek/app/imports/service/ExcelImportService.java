package com.ek.app.imports.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.ek.app.customer.entity.Customer;
import com.ek.app.customer.repository.CustomerRepository;
import com.ek.app.billing.app.BillingUseCase;
import com.ek.app.billing.domain.BillHeaderDTO;
import com.ek.app.billing.domain.BillItemDTO;
import com.ek.app.imports.dto.ImportRowDTO;
import com.ek.app.imports.dto.ImportSummaryResponse;
import com.ek.app.imports.util.ExcelParser;
import com.ek.app.inventory.domain.InventoryMovementDto;
import com.ek.app.inventory.domain.InventoryService;
import com.ek.app.inventory.domain.InventoryType;
import com.ek.app.productcatalog.infra.db.Product;
import com.ek.app.productcatalog.infra.db.ProductRepository;
import com.ek.app.sales.entity.PaymentStatus;
import com.ek.app.sales.entity.SalesOrder;
import com.ek.app.sales.entity.SalesOrderItem;
import com.ek.app.sales.entity.SalesOrderStatus;
import com.ek.app.sales.repository.SalesOrderRepository;
import com.ek.app.sales.service.AuthContextService;

@Service
public class ExcelImportService {

    private static final Pattern GST_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");
    private static final int CHUNK_SIZE = 50;

    private final ExcelParser excelParser;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final InventoryService inventoryService;
    private final AuthContextService authContextService;
    private final BillingUseCase billingUseCase;
    private final TransactionTemplate transactionTemplate;

    private final String companyState;

    public ExcelImportService(
            ExcelParser excelParser,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            SalesOrderRepository salesOrderRepository,
            InventoryService inventoryService,
            AuthContextService authContextService,
            BillingUseCase billingUseCase,
            PlatformTransactionManager transactionManager,
            @Value("${company.state:Haryana}") String companyState) {
        this.excelParser = excelParser;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.inventoryService = inventoryService;
        this.authContextService = authContextService;
        this.billingUseCase = billingUseCase;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.companyState = companyState;
    }

    public ImportSummaryResponse importSalesOrders(MultipartFile file) {
        if (!authContextService.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin can import data");
        }

        List<ImportRowDTO> rows = excelParser.parse(file);
        ImportSummaryResponse summary = new ImportSummaryResponse();
        summary.setTotalRecords(rows.size());

        for (int start = 0; start < rows.size(); start += CHUNK_SIZE) {
            int end = Math.min(rows.size(), start + CHUNK_SIZE);
            List<ImportRowDTO> chunk = new ArrayList<>(rows.subList(start, end));
            transactionTemplate.executeWithoutResult(status -> processChunk(chunk, summary));
        }

        return summary;
    }

    private void processChunk(List<ImportRowDTO> rows, ImportSummaryResponse summary) {
        for (ImportRowDTO row : rows) {
            try {
                importRow(row);
                summary.addSuccess();
            } catch (Exception ex) {
                summary.addFailure(row.getRowNumber(), ex.getMessage());
            }
        }
    }

    private void importRow(ImportRowDTO row) {
        validateRow(row);

        Customer customer = resolveCustomer(row.getCustomerName());
        Product product = resolveProduct(row);

        BigDecimal gstRate = resolveGstRate(product);
        BigDecimal totalInclusive = row.getTotalPrice().setScale(2, RoundingMode.HALF_UP);

        BigDecimal baseTotal = totalInclusive
                .divide(BigDecimal.ONE.add(gstRate.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP)),
                        2,
                        RoundingMode.HALF_UP);
        BigDecimal gstAmount = totalInclusive.subtract(baseTotal).setScale(2, RoundingMode.HALF_UP);

        boolean intraState = isIntraState(customer);
        BigDecimal cgst = BigDecimal.ZERO;
        BigDecimal sgst = BigDecimal.ZERO;
        BigDecimal igst = BigDecimal.ZERO;

        if (intraState) {
            cgst = gstAmount.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
            sgst = gstAmount.subtract(cgst).setScale(2, RoundingMode.HALF_UP);
        } else {
            igst = gstAmount;
        }

        BigDecimal unitBasePrice = baseTotal.divide(row.getQuantity(), 4, RoundingMode.HALF_UP);

        SalesOrder order = new SalesOrder();
        order.setOrderNumber("IMP-" + row.getOrderDate().format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomer(customer);
        order.setCreatedBy(authContextService.username());
        order.setOrderDate(row.getOrderDate());
        order.setStatus(SalesOrderStatus.DELIVERED);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setTotalAmount(totalInclusive);

        SalesOrderItem item = new SalesOrderItem();
        item.setSalesOrder(order);
        item.setProduct(product);
        item.setQuantity(row.getQuantity().setScale(2, RoundingMode.HALF_UP));
        item.setPrice(unitBasePrice);
        item.setBaseAmount(baseTotal);
        item.setGstRate(gstRate);
        item.setGstAmount(gstAmount);
        item.setCgstAmount(cgst);
        item.setSgstAmount(sgst);
        item.setIgstAmount(igst);
        item.setTotal(totalInclusive);

        order.getItems().add(item);
        SalesOrder savedOrder = salesOrderRepository.save(order);

        BillHeaderDTO bill = new BillHeaderDTO();
        bill.setBillNo("INV-IMP-" + row.getOrderDate().format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
            + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bill.setCustomerId(customer.getId());
        bill.setCustomerName(customer.getName());
        bill.setCustomerPhone(customer.getPhone());
        bill.setCustomerState(customer.getState());
        bill.setBillDate(row.getOrderDate().atStartOfDay());
        bill.setPaymentMode("IMPORTED");
        bill.setStatus("PAID");

        BillItemDTO billItem = new BillItemDTO();
        billItem.setProductId(product.getProductId());
        billItem.setSku(product.getSku());
        billItem.setQuantity(row.getQuantity());
        // BillingUseCase expects pre-tax unit price; GST is derived from product tax_code.
        billItem.setUnitPrice(unitBasePrice.setScale(2, RoundingMode.HALF_UP));
        bill.setItems(List.of(billItem));

        Long billId = billingUseCase.createBillFromProductNames(bill);

        InventoryMovementDto movement = new InventoryMovementDto();
        movement.setProductId(product.getProductId());
        movement.setMovementType(InventoryType.OUT);
        movement.setQuantity(row.getQuantity());
        movement.setReference("IMPORT-" + savedOrder.getOrderNumber() + "-BILL-" + billId);
        movement.setLocation(row.getChannel() == null || row.getChannel().isBlank() ? "IMPORT" : row.getChannel());
        inventoryService.updateStock(movement);
    }

    private Customer resolveCustomer(String customerName) {
        return customerRepository.findFirstByNameIgnoreCase(customerName)
                .orElseGet(() -> {
                    Customer customer = new Customer();
                    customer.setCpId("AUTO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    customer.setName(customerName);
                    customer.setPhone("0000000000");
                    customer.setState(companyState);
                    customer.setCountry("India");
                    return customerRepository.save(customer);
                });
    }

    private Product resolveProduct(ImportRowDTO row) {
        if (row.getSku() != null && !row.getSku().isBlank()) {
            return productRepository.findBySkuIgnoreCase(row.getSku().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found for SKU: " + row.getSku()));
        }

        List<Product> byName = productRepository.findByNameOrTitleExactIgnoreCase(row.getProductName().trim());
        if (!byName.isEmpty()) {
            return byName.get(0);
        }

        throw new IllegalArgumentException("Product not found for name: " + row.getProductName());
    }

    private BigDecimal resolveGstRate(Product product) {
        String taxCode = product.getTax_code();
        if (taxCode == null || taxCode.isBlank()) {
            throw new IllegalArgumentException("tax_code is required for product sku: " + product.getSku());
        }

        Matcher matcher = GST_PATTERN.matcher(taxCode);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid tax_code for product sku: " + product.getSku() + " (" + taxCode + ")");
        }

        BigDecimal gstRate = new BigDecimal(matcher.group(1));
        if (gstRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid negative GST in tax_code for sku: " + product.getSku());
        }
        return gstRate;
    }

    private boolean isIntraState(Customer customer) {
        String customerState = customer.getState();
        if (customerState == null || customerState.isBlank()) {
            return true;
        }
        return customerState.trim().equalsIgnoreCase(companyState.trim());
    }

    private void validateRow(ImportRowDTO row) {
        if (row.getCustomerName() == null || row.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if ((row.getSku() == null || row.getSku().isBlank())
                && (row.getProductName() == null || row.getProductName().isBlank())) {
            throw new IllegalArgumentException("Either SKU or product name is required");
        }
        if (row.getQuantity() == null || row.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (row.getTotalPrice() == null || row.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total price must be greater than zero");
        }
        if (row.getOrderDate() == null) {
            row.setOrderDate(LocalDate.now());
        }

        Objects.requireNonNull(row.getOrderDate(), "Order date is required");
    }
}
