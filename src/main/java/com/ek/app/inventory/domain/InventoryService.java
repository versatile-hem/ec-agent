package com.ek.app.inventory.domain;

import java.util.List;

import com.ek.app.inventory.infra.db.InventoryPosition;

public interface InventoryService {

    

    InventoryMovementDto addInventory(InventoryMovementDto itemDto);
    InventoryMovementDto updateInventory(InventoryMovementDto itemDto);

    void updateStock(InventoryMovementDto movementDto, Long productId, Integer qty, InventoryType type);

    InventoryMovementDto getById(String id);
    List<InventoryMovementDto> listAll(int page, int size);

    List<InventoryMovementDto> search(String skuLike, String nameLike, String locationLike, Boolean active, int page, int size);

    void delete(String id);

    // Stock ops
    InventoryMovementDto adjustStock(String id, int delta); // positive = increase, negative = decrease
    InventoryMovementDto reserveStock(String id, int qty);
    InventoryMovementDto releaseReserved(String id, int qty);
    void transferStock(String fromId, String toId, int qty);

    // Idempotent add/upsert by SKU+location
    InventoryMovementDto upsertBySkuLocation(String sku, String name, String location, int qtyDelta, String uom, String metadata);

    
    InventoryPosition findInventoryPosition(int product_id);

    /**
     * 
     * @param product_id
     * @return
     */
    List<InventoryMovementDto> searchStockFlow(Long product_id);


}
