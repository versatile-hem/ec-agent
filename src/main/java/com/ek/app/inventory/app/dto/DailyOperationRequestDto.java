package com.ek.app.inventory.app.dto;

import java.math.BigDecimal;

import com.ek.app.inventory.infra.db.SalesChannel;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DailyOperationRequestDto {

    public enum OperationType {
        ORDER,
        RETURN
    }

    @NotNull
    private OperationType type;

    @NotNull
    private Long productId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal quantity;

    @NotBlank
    private String unit;

    private String courier;

    private SalesChannel channel;
}
