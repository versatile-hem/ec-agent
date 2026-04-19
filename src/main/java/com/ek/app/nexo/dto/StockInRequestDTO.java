package com.ek.app.nexo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockInRequestDTO {

    @NotNull
    private Long productId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal quantity;

    @NotBlank
    private String unit;

    private String supplier;

    private String batchNumber;

    private String warehouseId;
}
