package com.ek.app.inventory.app.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryBalanceResponseDto {

    private Long productId;

    private BigDecimal quantity;
}
