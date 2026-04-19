package com.ek.app.sales.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ek.app.sales.entity.Payment;

public interface SalesPaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findBySalesOrder_IdOrderByPaymentDateDescCreatedAtDesc(Long orderId);

    @Query("select coalesce(sum(p.amount), 0) from SalesPayment p where p.salesOrder.id = :orderId")
    BigDecimal sumPaidByOrderId(@Param("orderId") Long orderId);
}
