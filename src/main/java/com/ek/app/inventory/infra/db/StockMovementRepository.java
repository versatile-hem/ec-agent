package com.ek.app.inventory.infra.db;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @Query("SELECT sm FROM StockMovement sm WHERE sm.type = :type")
    Page<StockMovement> findByType(
            @Param("type") StockMovement.StockMovementType type,
            Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.createdAt BETWEEN :startDate AND :endDate")
    Page<StockMovement> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.type = :type AND sm.createdAt BETWEEN :startDate AND :endDate")
    Page<StockMovement> findByTypeAndDateRange(
            @Param("type") StockMovement.StockMovementType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.reference = :reference")
    Page<StockMovement> findByReference(
            @Param("reference") StockMovement.StockMovementReference reference,
            Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.createdBy = :userId ORDER BY sm.createdAt DESC")
    Page<StockMovement> findByCreatedBy(
            @Param("userId") String userId,
            Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm ORDER BY sm.createdAt DESC")
    Page<StockMovement> findAllOrderByCreatedAtDesc(Pageable pageable);
}
