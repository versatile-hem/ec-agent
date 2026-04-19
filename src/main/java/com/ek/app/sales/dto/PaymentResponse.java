package com.ek.app.sales.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.ek.app.sales.entity.PaymentMode;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMode mode;
    private String collectedBy;
    private Instant createdAt;
}
