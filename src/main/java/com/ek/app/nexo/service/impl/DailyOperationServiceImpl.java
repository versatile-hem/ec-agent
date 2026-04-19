package com.ek.app.nexo.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ek.app.nexo.dto.DailyOperationDTO;
import com.ek.app.nexo.dto.InventoryDTO;
import com.ek.app.nexo.entity.DailyOperation;
import com.ek.app.nexo.entity.DailyOperationType;
import com.ek.app.nexo.entity.Product;
import com.ek.app.nexo.exception.ResourceNotFoundException;
import com.ek.app.nexo.repository.NexoDailyOperationRepository;
import com.ek.app.nexo.repository.NexoProductRepository;
import com.ek.app.nexo.service.DailyOperationService;
import com.ek.app.nexo.service.InventoryService;

@Service("nexoDailyOperationServiceImpl")
public class DailyOperationServiceImpl implements DailyOperationService {

    private final NexoDailyOperationRepository dailyOperationRepository;
    private final NexoProductRepository productRepository;
    private final InventoryService inventoryService;

    public DailyOperationServiceImpl(
            NexoDailyOperationRepository dailyOperationRepository,
            NexoProductRepository productRepository,
            InventoryService inventoryService) {
        this.dailyOperationRepository = dailyOperationRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    @Override
    @Transactional
    public InventoryDTO process(DailyOperationDTO request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));

        DailyOperation operation = new DailyOperation();
        operation.setType(request.getType());
        operation.setProduct(product);
        operation.setQuantity(request.getQuantity());
        operation.setUnit(request.getUnit());
        operation.setCourier(request.getCourier());
        operation.setChannel(request.getChannel());
        operation.setWarehouseId(request.getWarehouseId());
        dailyOperationRepository.save(operation);

        if (request.getType() == DailyOperationType.ORDER) {
            return inventoryService.decreaseStock(request.getProductId(), request.getQuantity(), request.getWarehouseId());
        }

        return inventoryService.increaseStock(request.getProductId(), request.getQuantity(), request.getWarehouseId());
    }
}
