package com.ek.app.inventory.infra.db;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ek.app.inventory.domain.InventoryType;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

  // --- Create/Update ---
  InventoryMovement save(InventoryMovement item); // upsert by id

  @Query(value = "SELECT * FROM inventory_movement WHERE product_id = :productId ORDER BY movement_time DESC", countQuery = "SELECT count(*) FROM inventory_movement WHERE product_id = :productId", nativeQuery = true)
  Page<InventoryMovement> findByProduct_Id(Long productId, Pageable pageable);

  // --- Read: New query methods for stock movement API ---
  
  /**
   * Find all inventory movements by type (IN/OUT/RETURN/DAMAGE/ADJUST) - paginated
   */
  @Query("SELECT im FROM InventoryMovement im WHERE im.movementType = :type ORDER BY im.movementTime DESC")
  Page<InventoryMovement> findByMovementType(
          @Param("type") InventoryType type,
          Pageable pageable);

  /**
   * Find all inventory movements within a date range - paginated
   */
  @Query("SELECT im FROM InventoryMovement im WHERE im.movementTime BETWEEN :startDate AND :endDate ORDER BY im.movementTime DESC")
  Page<InventoryMovement> findByMovementTimeRange(
          @Param("startDate") LocalDateTime startDate,
          @Param("endDate") LocalDateTime endDate,
          Pageable pageable);

  /**
   * Find inventory movements by type AND date range - paginated
   */
  @Query("SELECT im FROM InventoryMovement im WHERE im.movementType = :type AND im.movementTime BETWEEN :startDate AND :endDate ORDER BY im.movementTime DESC")
  Page<InventoryMovement> findByMovementTypeAndMovementTimeRange(
          @Param("type") InventoryType type,
          @Param("startDate") LocalDateTime startDate,
          @Param("endDate") LocalDateTime endDate,
          Pageable pageable);

  /**
   * Find all inventory movements created by a specific user - paginated
   */
  @Query("SELECT im FROM InventoryMovement im WHERE im.createdBy = :userId ORDER BY im.movementTime DESC")
  Page<InventoryMovement> findByCreatedBy(
          @Param("userId") String userId,
          Pageable pageable);

  /**
   * Find all inventory movements ordered by movement time descending - paginated
   */
  @Query("SELECT im FROM InventoryMovement im ORDER BY im.movementTime DESC")
  Page<InventoryMovement> findAllOrderByMovementTimeDesc(Pageable pageable);

  // --- Read: Legacy methods ---
  List<InventoryMovement> findAll();

  // --- Count ---
  long count();

}
