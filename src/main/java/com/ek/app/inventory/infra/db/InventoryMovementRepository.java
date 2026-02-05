package com.ek.app.inventory.infra.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryMovementRepository   extends JpaRepository<InventoryMovement, Long>{


  // --- Create/Update ---
    InventoryMovement save(InventoryMovement item);                  // upsert by id
   // List<InventoryMovement> saveAll(List<InventoryItem> items);

    // --- Read ---
    //Optional<InventoryMovement> findById(String id);
   // Optional<InventoryMovement> findBySkuAndLocation(String sku, String location);
    List<InventoryMovement> findAll();

    /**
     * Flexible search with optional filters.
     * Any null argument means "ignore this filter".
     */
   // List<InventoryMovement> search(String skuLike, String nameLike, String locationLike, Boolean active);

    // --- Delete ---
  //  void deleteById(String id);
   // long deleteAllBySku(String sku); // convenience if you delete all locations for a SKU

    // --- Existence checks ---
  //  boolean existsById(String id);
   // boolean existsBySkuAndLocation(String sku, String location);

    // --- Count ---
    long count();


}
