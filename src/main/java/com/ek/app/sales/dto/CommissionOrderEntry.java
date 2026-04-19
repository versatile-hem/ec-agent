package com.ek.app.sales.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommissionOrderEntry {

    private Long orderId;
    private BigDecimal amount;
    private BigDecimal commission;
}
