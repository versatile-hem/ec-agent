package com.ek.app.inventory.app.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductQuantityInsightDto {

    private Long productId;

    private String productName;

    private BigDecimal quantity;
}
