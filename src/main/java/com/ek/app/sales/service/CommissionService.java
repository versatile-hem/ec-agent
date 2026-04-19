package com.ek.app.sales.service;

import com.ek.app.sales.dto.MonthlyCommissionResponse;

public interface CommissionService {

    MonthlyCommissionResponse getMonthlyCommission(String userId, String month);
}
