package com.ek.app.billing.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ek.app.billing.domain.BillHeaderDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Bill API", description = "Generate invoice using Freemarker")
@RequestMapping("/api/billing")
public class BillingController {


    @Autowired
    private BillingUseCase billingUseCase;


    @PostMapping(value = "/generate", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
    @Operation(
        summary = "Generate Bill",
        description = "Generates invoice PDF file"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bill generated successfully",
            content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<byte[]>  generateBill(@RequestBody BillHeaderDTO request) {
    long l = billingUseCase.createBill(request);
    request.setId(l);
    byte[] fileData = billingUseCase.generateBill(request);
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill.pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(fileData);
    }

    @PostMapping(value = "/v1/generate", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
    @Operation(
        summary = "Generate Bill V1",
        description = "Generates invoice PDF by accepting sku and unitPrice in items and resolving product details from table. Defaults GST to 5% when item gst is not provided",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Provide sku and unitPrice in items. If gst is omitted at item level, 5% is auto-applied.",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "V1 SKU Price Request",
                    value = "{\n  \"billNo\": \"INV-V1-5003\",\n  \"customerName\": \"V1 Customer\",\n  \"customerPhone\": \"9999999999\",\n  \"billDate\": \"2026-04-17T19:45:00\",\n  \"discountAmount\": 10,\n  \"paymentMode\": \"UPI\",\n  \"status\": \"PAID\",\n  \"items\": [\n    {\n      \"sku\": \"SKU-1001\",\n      \"unitPrice\": 600,\n      \"quantity\": 2\n    }\n  ]\n}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bill generated successfully",
            content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Product not found for sku"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<byte[]> generateBillV1(@RequestBody BillHeaderDTO request) {
        long id = billingUseCase.createBillFromProductNames(request);
        request.setId(id);
        byte[] fileData = billingUseCase.generateBill(request);
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill.pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(fileData);
    }

    @PostMapping(value = "/v2/generate", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
    @Operation(
        summary = "Generate Multi Page Bill V2",
        description = "Accepts an array of BillHeaderDTO, creates each bill using v1 flow and returns a single merged multi-page PDF"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Multi-page bill generated successfully",
            content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Product not found for sku"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<byte[]> generateBillV2(@RequestBody List<BillHeaderDTO> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one bill request is required");
        }

        try {
            PDFMergerUtility merger = new PDFMergerUtility();
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            for (BillHeaderDTO request : requests) {
                long id = billingUseCase.createBillFromProductNames(request);
                request.setId(id);
                byte[] fileData = billingUseCase.generateBill(request);
                merger.addSource(new ByteArrayInputStream(fileData));
            }

            merger.setDestinationStream(output);
            merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bills.pdf")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(output.toByteArray());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating multi-page bill", ex);
        }
    }
 


    

}