package com.ek.app.sales.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyCommissionResponse {

    private BigDecimal totalCommission;
    private List<CommissionOrderEntry> orders;
}
