package com.ek.app.nexo.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.ek.app.nexo.entity.DailyOperationType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DailyOperationDTO {

    private Long id;

    @NotNull
    private DailyOperationType type;

    @NotNull
    private Long productId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal quantity;

    @NotBlank
    private String unit;

    private String courier;

    private String channel;

    private String warehouseId;

    private String createdBy;

    private Instant createdAt;

    private Instant updatedAt;
}
