package com.ek.app.billing.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class InvoiceUpdateRequest {

    @Schema(example = "UPI")
    private String paymentMode;

    @Schema(example = "PAID")
    private String status;
}
