package com.ek.app.billing.app;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.billing.api.BillingApiMapper;
import com.ek.app.billing.api.dto.BillingGenerateRequest;
import com.ek.app.billing.api.dto.BillingGenerateResponse;
import com.ek.app.billing.api.dto.ClientResponse;
import com.ek.app.billing.api.dto.InvoiceResponse;
import com.ek.app.billing.api.dto.InvoiceUpdateRequest;
import com.ek.app.billing.domain.BillHeaderDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@Validated
@Tag(name = "Billing API", description = "Billing and invoice management endpoints")
@RequestMapping("/api")
public class BillingController {

    private final BillingUseCase billingUseCase;
    private final BillingApiMapper mapper;

    public BillingController(BillingUseCase billingUseCase, BillingApiMapper mapper) {
        this.billingUseCase = billingUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/billing/generate")
    @Operation(summary = "Generate invoice", description = "Creates an invoice using SKU-based item request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Invoice generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BillingGenerateResponse> generateBill(@Valid @RequestBody BillingGenerateRequest request) {
        BillHeaderDTO domainRequest = mapper.toDomain(request);
        Long id = billingUseCase.createBillFromProductNames(domainRequest);
        domainRequest.setId(id);

        BillingGenerateResponse response = new BillingGenerateResponse(
                id,
                domainRequest.getBillNo(),
                domainRequest.getTotalAmount(),
                "/api/invoice/" + id,
                "/api/invoice/" + id + "/pdf",
                "CREATED");

        return ResponseEntity.created(URI.create("/api/invoice/" + id)).body(response);
    }

    @GetMapping("/invoice/{id}")
    @Operation(summary = "Get invoice", description = "Fetch an invoice by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice found"),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    public InvoiceResponse getInvoice(@PathVariable @NotNull Long id) {
        return mapper.toInvoiceResponse(billingUseCase.getBillById(id));
    }

    @GetMapping("/invoice")
    @Operation(summary = "List invoices", description = "List invoices by date range")
    public List<InvoiceResponse> listInvoices(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LocalDate start = fromDate == null ? LocalDate.now().minusMonths(1) : fromDate;
        LocalDate end = toDate == null ? LocalDate.now() : toDate;

        return billingUseCase.listBills(start, end)
                .stream()
                .map(mapper::toInvoiceResponse)
                .toList();
    }

    @GetMapping(value = "/invoice/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Download invoice PDF", description = "Generates invoice PDF for an existing invoice")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF generated successfully"),
            @ApiResponse(responseCode = "404", description = "Invoice not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable @NotNull Long id) {
        BillHeaderDTO bill = billingUseCase.getBillById(id);
        byte[] fileData = billingUseCase.generateBill(bill);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(fileData);
    }

    @PutMapping("/invoice/{id}")
    @Operation(summary = "Update invoice", description = "Update invoice payment mode and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice updated successfully"),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    public InvoiceResponse updateInvoice(
            @PathVariable @NotNull Long id,
            @RequestBody InvoiceUpdateRequest request) {

        BillHeaderDTO updated = billingUseCase.updateBill(id, request.getPaymentMode(), request.getStatus());
        return mapper.toInvoiceResponse(updated);
    }

    @DeleteMapping("/invoice/{id}")
    @Operation(summary = "Delete invoice", description = "Delete invoice by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Invoice deleted"),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    public ResponseEntity<Void> deleteInvoice(@PathVariable @NotNull Long id) {
        billingUseCase.deleteBill(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client")
    @Operation(summary = "List clients", description = "Fetch unique clients from billing records")
    public List<ClientResponse> listClients(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LocalDate start = fromDate == null ? LocalDate.now().minusMonths(3) : fromDate;
        LocalDate end = toDate == null ? LocalDate.now() : toDate;
        String query = q == null ? "" : q.trim().toLowerCase();

        return billingUseCase.listBills(start, end)
                .stream()
                .filter(b -> !query.isBlank()
                        ? containsIgnoreCase(b.getCustomerName(), query) || containsIgnoreCase(b.getCustomerPhone(), query)
                        : true)
                .map(b -> new ClientResponse(b.getCustomerName(), b.getCustomerPhone()))
                .filter(c -> c.getCustomerName() != null && !c.getCustomerName().isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

}