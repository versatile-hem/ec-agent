package com.ek.app.inventory.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementItemResponse {

    private Long id;

    private Long productId;

    private String productName;

    private String sku;

    private BigDecimal quantity;

    private String reference;
}
