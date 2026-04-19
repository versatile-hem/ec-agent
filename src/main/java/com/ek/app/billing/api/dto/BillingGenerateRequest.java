package com.ek.app.billing.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BillingGenerateRequest {

    @Schema(example = "INV-V1-5003")
    @NotBlank(message = "billNo is required")
    private String billNo;

    @Schema(example = "V1 Customer")
    @NotBlank(message = "customerName is required")
    private String customerName;

    @Schema(example = "9999999999")
    @NotBlank(message = "customerPhone is required")
    private String customerPhone;

    @Schema(example = "2026-04-17T19:45:00")
    @NotNull(message = "billDate is required")
    private LocalDateTime billDate;

    @Schema(example = "10")
    private BigDecimal discountAmount;

    @Schema(example = "UPI")
    private String paymentMode;

    @Schema(example = "PAID")
    private String status;

    @Valid
    @NotEmpty(message = "items are required")
    private List<BillItemRequest> items;
}
