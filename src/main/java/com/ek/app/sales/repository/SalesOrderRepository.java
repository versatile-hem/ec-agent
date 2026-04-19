package com.ek.app.sales.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ek.app.sales.entity.SalesOrder;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    List<SalesOrder> findByCreatedByOrderByCreatedAtDesc(String createdBy);
}
