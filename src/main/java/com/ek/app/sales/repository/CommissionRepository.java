package com.ek.app.sales.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ek.app.sales.entity.Commission;

public interface CommissionRepository extends JpaRepository<Commission, Long> {

    List<Commission> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(String userId, Instant from, Instant to);
}
