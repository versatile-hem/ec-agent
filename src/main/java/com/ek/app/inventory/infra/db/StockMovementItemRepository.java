package com.ek.app.inventory.infra.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementItemRepository extends JpaRepository<StockMovementItem, Long> {

    List<StockMovementItem> findByMovementId(Long movementId);

    @Query("SELECT smi FROM StockMovementItem smi WHERE smi.product.productId = :productId")
    List<StockMovementItem> findByProductId(@Param("productId") Long productId);

    @Query("SELECT smi FROM StockMovementItem smi WHERE smi.movement.id = :movementId AND smi.product.productId = :productId")
    StockMovementItem findByMovementAndProduct(
            @Param("movementId") Long movementId,
            @Param("productId") Long productId);
}
