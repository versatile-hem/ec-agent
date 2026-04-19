package com.ek.app.nexo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ek.app.nexo.dto.InventoryDTO;
import com.ek.app.nexo.dto.StockInRequestDTO;
import com.ek.app.nexo.entity.Product;
import com.ek.app.nexo.entity.StockIn;
import com.ek.app.nexo.exception.ResourceNotFoundException;
import com.ek.app.nexo.repository.NexoProductRepository;
import com.ek.app.nexo.repository.NexoStockInRepository;
import com.ek.app.nexo.service.InventoryService;
import com.ek.app.nexo.service.StockInService;

@Service("nexoStockInServiceImpl")
public class StockInServiceImpl implements StockInService {

    private final NexoStockInRepository stockInRepository;
    private final NexoProductRepository productRepository;
    private final InventoryService inventoryService;

    public StockInServiceImpl(
            NexoStockInRepository stockInRepository,
            NexoProductRepository productRepository,
            InventoryService inventoryService) {
        this.stockInRepository = stockInRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    @Override
    @Transactional
    public List<InventoryDTO> stockIn(List<StockInRequestDTO> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Stock in request cannot be empty");
        }

        List<InventoryDTO> updatedInventories = new ArrayList<>();
        for (StockInRequestDTO request : requests) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));

            StockIn stockIn = new StockIn();
            stockIn.setProduct(product);
            stockIn.setQuantity(request.getQuantity());
            stockIn.setUnit(request.getUnit());
            stockIn.setSupplier(request.getSupplier());
            stockIn.setBatchNumber(request.getBatchNumber());
            stockIn.setWarehouseId(request.getWarehouseId());
            stockInRepository.save(stockIn);

            updatedInventories.add(inventoryService.increaseStock(
                    request.getProductId(),
                    request.getQuantity(),
                    request.getWarehouseId()));
        }

        return updatedInventories;
    }
}
