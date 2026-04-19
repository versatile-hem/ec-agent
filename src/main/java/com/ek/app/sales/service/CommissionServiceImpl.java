package com.ek.app.sales.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ek.app.sales.dto.CommissionOrderEntry;
import com.ek.app.sales.dto.MonthlyCommissionResponse;
import com.ek.app.sales.repository.CommissionRepository;

@Service
public class CommissionServiceImpl implements CommissionService {

    private final CommissionRepository commissionRepository;
    private final AuthContextService authContextService;

    public CommissionServiceImpl(CommissionRepository commissionRepository, AuthContextService authContextService) {
        this.commissionRepository = commissionRepository;
        this.authContextService = authContextService;
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyCommissionResponse getMonthlyCommission(String userId, String month) {
        if (month == null || month.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "month is required in format yyyy-MM");
        }

        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(month);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid month format. Use yyyy-MM");
        }

        String effectiveUser = userId;
        if (!authContextService.isAdmin()) {
            effectiveUser = authContextService.username();
        }

        if (effectiveUser == null || effectiveUser.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }

        var from = yearMonth.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        var to = yearMonth.plusMonths(1).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        var entries = commissionRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(effectiveUser, from, to)
                .stream()
                .map(c -> CommissionOrderEntry.builder()
                        .orderId(c.getSalesOrder().getId())
                        .amount(c.getSalesOrder().getTotalAmount())
                        .commission(c.getCommissionAmount())
                        .build())
                .toList();

        BigDecimal totalCommission = entries.stream()
                .map(CommissionOrderEntry::getCommission)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return MonthlyCommissionResponse.builder()
                .totalCommission(totalCommission)
                .orders(entries)
                .build();
    }
}
