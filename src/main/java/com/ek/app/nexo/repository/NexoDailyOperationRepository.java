package com.ek.app.nexo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ek.app.nexo.entity.DailyOperation;

public interface NexoDailyOperationRepository extends JpaRepository<DailyOperation, Long> {
}
