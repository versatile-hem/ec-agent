package com.ek.app.billing.api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BillItemResponse {

    private Long productId;

    @Schema(example = "Industrial Label Roll")
    private String productName;

    @Schema(example = "SKU-1001")
    private String sku;

    @Schema(example = "2")
    private BigDecimal quantity;

    @Schema(example = "600")
    private BigDecimal unitPrice;

    @Schema(example = "1200")
    private BigDecimal taxableValue;

    @Schema(example = "18")
    private BigDecimal gst;

    @Schema(example = "216")
    private BigDecimal tax;

    @Schema(example = "1416")
    private BigDecimal finalAmount;
}
