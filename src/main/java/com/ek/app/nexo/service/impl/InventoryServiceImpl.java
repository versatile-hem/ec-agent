package com.ek.app.nexo.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ek.app.nexo.dto.InventoryDTO;
import com.ek.app.nexo.entity.Inventory;
import com.ek.app.nexo.entity.Product;
import com.ek.app.nexo.exception.InsufficientStockException;
import com.ek.app.nexo.exception.ResourceNotFoundException;
import com.ek.app.nexo.repository.NexoInventoryRepository;
import com.ek.app.nexo.repository.NexoProductRepository;
import com.ek.app.nexo.service.InventoryService;

@Service("nexoInventoryServiceImpl")
public class InventoryServiceImpl implements InventoryService {

    private final NexoInventoryRepository inventoryRepository;
    private final NexoProductRepository productRepository;

    public InventoryServiceImpl(NexoInventoryRepository inventoryRepository, NexoProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public InventoryDTO increaseStock(Long productId, BigDecimal qty, String warehouseId) {
        if (qty == null || qty.signum() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        Inventory inventory = inventoryRepository.findForUpdate(productId, warehouseId)
                .orElseGet(() -> {
                    Inventory newInventory = new Inventory();
                    newInventory.setProduct(product);
                    newInventory.setWarehouseId(warehouseId);
                    newInventory.setQuantity(BigDecimal.ZERO);
                    return newInventory;
                });

        inventory.setQuantity(inventory.getQuantity().add(qty));
        return toDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryDTO decreaseStock(Long productId, BigDecimal qty, String warehouseId) {
        if (qty == null || qty.signum() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Inventory inventory = inventoryRepository.findForUpdate(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getQuantity().compareTo(qty) < 0) {
            throw new InsufficientStockException(
                    "Insufficient stock for product " + productId + ". Available: " + inventory.getQuantity());
        }

        inventory.setQuantity(inventory.getQuantity().subtract(qty));
        return toDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getInventoryByProduct(Long productId) {
        return inventoryRepository.findByProduct_Id(productId).stream().map(this::toDto).toList();
    }

    private InventoryDTO toDto(Inventory inventory) {
        return InventoryDTO.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .quantity(inventory.getQuantity())
                .warehouseId(inventory.getWarehouseId())
                .lastUpdated(inventory.getLastUpdated())
                .createdBy(inventory.getCreatedBy())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
