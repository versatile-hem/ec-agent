package com.ek.app.sales.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesOrderItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal total;
}
