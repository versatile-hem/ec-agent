package com.ek.app.billing.api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillingGenerateResponse {

    @Schema(example = "101")
    private Long invoiceId;

    @Schema(example = "INV-V1-5003")
    private String billNo;

    @Schema(example = "1270.00")
    private BigDecimal totalAmount;

    @Schema(example = "/api/invoice/101")
    private String invoiceUrl;

    @Schema(example = "/api/invoice/101/pdf")
    private String pdfUrl;

    @Schema(example = "CREATED")
    private String status;
}
