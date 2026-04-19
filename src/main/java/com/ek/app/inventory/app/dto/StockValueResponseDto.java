package com.ek.app.inventory.app.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockValueResponseDto {

    private BigDecimal totalValue;
}
