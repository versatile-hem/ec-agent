package com.ek.app.billing.api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BillItemRequest {

    @Schema(example = "SKU-1001")
    @NotBlank(message = "sku is required")
    private String sku;

    @Schema(example = "2")
    @NotNull(message = "quantity is required")
    @DecimalMin(value = "0.0001", message = "quantity must be greater than 0")
    private BigDecimal quantity;

    @Schema(example = "600")
    @NotNull(message = "unitPrice is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "unitPrice must be greater than 0")
    private BigDecimal unitPrice;
}
