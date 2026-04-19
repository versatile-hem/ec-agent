package com.ek.app.nexo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ek.app.nexo.entity.StockIn;

public interface NexoStockInRepository extends JpaRepository<StockIn, Long> {
}
