package com.ek.app.imports.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImportRowDTO {

    private int rowNumber;
    private String customerName;
    private String productName;
    private String sku;
    private BigDecimal quantity;
    private BigDecimal totalPrice;
    private LocalDate orderDate;
    private String channel;
}
