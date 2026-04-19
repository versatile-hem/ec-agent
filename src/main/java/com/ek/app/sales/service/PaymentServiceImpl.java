package com.ek.app.sales.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ek.app.sales.dto.AddPaymentRequest;
import com.ek.app.sales.dto.PaymentResponse;
import com.ek.app.sales.entity.Commission;
import com.ek.app.sales.entity.Payment;
import com.ek.app.sales.entity.PaymentStatus;
import com.ek.app.sales.repository.CommissionRepository;
import com.ek.app.sales.repository.SalesPaymentRepository;
import com.ek.app.sales.repository.SalesOrderRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final SalesPaymentRepository paymentRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final CommissionRepository commissionRepository;
    private final AuthContextService authContextService;

    @Value("${sales.commission.percentage:2.00}")
    private BigDecimal commissionPercentage;

    public PaymentServiceImpl(
            SalesPaymentRepository paymentRepository,
            SalesOrderRepository salesOrderRepository,
            CommissionRepository commissionRepository,
            AuthContextService authContextService) {
        this.paymentRepository = paymentRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.commissionRepository = commissionRepository;
        this.authContextService = authContextService;
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public PaymentResponse add(AddPaymentRequest request) {
        Long orderId = Objects.requireNonNull(request.getOrderId(), "orderId is required");
        var order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        BigDecimal alreadyPaid = paymentRepository.sumPaidByOrderId(order.getId());
        BigDecimal nextPaid = alreadyPaid.add(request.getAmount());

        if (nextPaid.compareTo(order.getTotalAmount()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment exceeds order total amount");
        }

        Payment payment = new Payment();
        payment.setSalesOrder(order);
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setMode(request.getMode());
        payment.setCollectedBy(authContextService.username());

        Payment saved = paymentRepository.save(payment);

        if (nextPaid.compareTo(BigDecimal.ZERO) <= 0) {
            order.setPaymentStatus(PaymentStatus.PENDING);
        } else if (nextPaid.compareTo(order.getTotalAmount()) < 0) {
            order.setPaymentStatus(PaymentStatus.PARTIAL);
        } else {
            order.setPaymentStatus(PaymentStatus.PAID);
        }
        salesOrderRepository.save(order);

        Commission commission = new Commission();
        commission.setUserId(saved.getCollectedBy());
        commission.setSalesOrder(order);
        commission.setPercentage(commissionPercentage);
        commission.setCommissionAmount(saved.getAmount()
                .multiply(commissionPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
        commissionRepository.save(commission);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public List<PaymentResponse> listByOrder(Long orderId) {
        Long nonNullOrderId = Objects.requireNonNull(orderId, "orderId is required");
        var order = salesOrderRepository.findById(nonNullOrderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!authContextService.isAdmin() && !order.getCreatedBy().equalsIgnoreCase(authContextService.username())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view payments for this order");
        }

        return paymentRepository.findBySalesOrder_IdOrderByPaymentDateDescCreatedAtDesc(nonNullOrderId)
                .stream().map(this::toResponse).toList();
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getSalesOrder().getId())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .mode(payment.getMode())
                .collectedBy(payment.getCollectedBy())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
