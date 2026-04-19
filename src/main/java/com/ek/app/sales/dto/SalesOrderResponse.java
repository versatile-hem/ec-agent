package com.ek.app.sales.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.ek.app.sales.entity.PaymentStatus;
import com.ek.app.sales.entity.SalesOrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesOrderResponse {

    private Long id;
    private String orderNumber;
    private Long customerId;
    private String createdBy;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private SalesOrderStatus status;
    private PaymentStatus paymentStatus;
    private Instant createdAt;
    private List<SalesOrderItemResponse> items;
}
