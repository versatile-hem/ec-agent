package com.ek.app.nexo.dto;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String sku;

    @NotBlank
    private String barcode;

    private String hsnCode;

    @NotBlank
    private String unit;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    private String createdBy;

    private Instant createdAt;

    private Instant updatedAt;
}
