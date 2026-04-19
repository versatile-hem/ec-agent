package com.ek.app.sales.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.sales.dto.MonthlyCommissionResponse;
import com.ek.app.sales.service.CommissionService;

@RestController
@RequestMapping("/api/commission")
public class CommissionController {

    private final CommissionService commissionService;

    public CommissionController(CommissionService commissionService) {
        this.commissionService = commissionService;
    }

    @GetMapping
    public MonthlyCommissionResponse monthly(
            @RequestParam(required = false) String userId,
            @RequestParam String month) {
        return commissionService.getMonthlyCommission(userId, month);
    }
}
