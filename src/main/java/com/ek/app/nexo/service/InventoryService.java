package com.ek.app.nexo.service;

import java.math.BigDecimal;
import java.util.List;

import com.ek.app.nexo.dto.InventoryDTO;

public interface InventoryService {

    InventoryDTO increaseStock(Long productId, BigDecimal qty, String warehouseId);

    InventoryDTO decreaseStock(Long productId, BigDecimal qty, String warehouseId);

    List<InventoryDTO> getInventoryByProduct(Long productId);
}
