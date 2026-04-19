package com.ek.app.nexo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ek.app.nexo.entity.Inventory;

import jakarta.persistence.LockModeType;

public interface NexoInventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByProduct_Id(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select i
            from Inventory i
            where i.product.id = :productId
              and ((:warehouseId is null and i.warehouseId is null) or i.warehouseId = :warehouseId)
            """)
    Optional<Inventory> findForUpdate(@Param("productId") Long productId, @Param("warehouseId") String warehouseId);
}
