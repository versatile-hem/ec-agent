package com.ek.app.inventory.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ek.app.inventory.app.dto.CreateStockMovementRequest;
import com.ek.app.inventory.app.dto.StockMovementItemRequest;
import com.ek.app.inventory.app.dto.StockMovementItemResponse;
import com.ek.app.inventory.app.dto.StockMovementPageResponse;
import com.ek.app.inventory.app.dto.StockMovementResponse;
import com.ek.app.inventory.infra.db.StockMovement;
import com.ek.app.inventory.infra.db.StockMovement.StockMovementReference;
import com.ek.app.inventory.infra.db.StockMovement.StockMovementType;
import com.ek.app.inventory.infra.db.StockMovementItem;
import com.ek.app.inventory.infra.db.StockMovementItemRepository;
import com.ek.app.inventory.infra.db.StockMovementRepository;
import com.ek.app.productcatalog.infra.db.Product;
import com.ek.app.productcatalog.infra.db.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final StockMovementItemRepository stockMovementItemRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    /**
     * Create a stock movement with items and update inventory
     */
    @Transactional
    public StockMovementResponse createStockMovement(CreateStockMovementRequest request, String userId) {
        log.info("Creating stock movement of type {} with {} items by user {}", 
                request.getType(), request.getItems().size(), userId);

        // Validate before processing
        validateRequest(request);

        // Create movement record
        StockMovement movement = StockMovement.builder()
                .type(request.getType())
                .reference(request.getReference())
                .notes(request.getNotes())
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        movement = stockMovementRepository.save(movement);
        log.info("Created stock movement with ID: {}", movement.getId());

        // Process each item and update inventory
        final StockMovement finalMovement = movement;
        final StockMovementType movementType = request.getType();
        List<StockMovementItem> items = request.getItems().stream()
                .map(itemRequest -> processStockItem(finalMovement, itemRequest, movementType))
                .collect(Collectors.toList());

        finalMovement.setItems(items);
        StockMovement savedMovement = stockMovementRepository.saveAndFlush(finalMovement);

        return mapToResponse(savedMovement);
    }

    /**
     * Process individual stock movement item and update inventory
     */
    private StockMovementItem processStockItem(StockMovement movement, 
                                               StockMovementItemRequest itemRequest, 
                                               StockMovementType type) {
        // Validate product exists
        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with ID: " + itemRequest.getProductId()));

        // Validate quantity
        if (itemRequest.getQuantity().signum() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0 for product: " + product.getName());
        }

        // Create movement item
        StockMovementItem item = StockMovementItem.builder()
                .movement(movement)
                .product(product)
                .quantity(itemRequest.getQuantity())
                .reference(itemRequest.getReference())
                .build();

        item = stockMovementItemRepository.save(item);

        // Update inventory using existing service
        updateInventory(product.getProductId(), itemRequest.getQuantity(), type);

        log.debug("Processed stock item for product {} with quantity {}", 
                product.getProductId(), itemRequest.getQuantity());

        return item;
    }

    /**
     * Update inventory position using existing InventoryService
     */
    private void updateInventory(Long productId, java.math.BigDecimal quantity, StockMovementType type) {
        InventoryMovementDto movementDto = new InventoryMovementDto();
        movementDto.setProductId(productId);
        movementDto.setQuantity(quantity);
        movementDto.setMovementType(type == StockMovementType.IN ? InventoryType.IN : InventoryType.OUT);
        movementDto.setReference("Stock Movement");
        movementDto.setMovementTime(LocalDateTime.now());

        inventoryService.updateStock(movementDto);
    }

    /**
     * Get stock movements with optional filtering
     */
    @Transactional(readOnly = true)
    public StockMovementPageResponse getStockMovements(
            StockMovementType type,
            LocalDate startDate,
            LocalDate endDate,
            int pageNumber,
            int pageSize) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNumber, pageSize);
        Page<StockMovement> page;

        if (type != null && startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            page = stockMovementRepository.findByTypeAndDateRange(type, startDateTime, endDateTime, pageable);
        } else if (type != null) {
            page = stockMovementRepository.findByType(type, pageable);
        } else if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            page = stockMovementRepository.findByDateRange(startDateTime, endDateTime, pageable);
        } else {
            page = stockMovementRepository.findAllOrderByCreatedAtDesc(pageable);
        }

        return StockMovementPageResponse.builder()
                .content(page.getContent().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isLast(page.isLast())
                .build();
    }

    /**
     * Get single stock movement by ID
     */
    @Transactional(readOnly = true)
    public StockMovementResponse getStockMovementById(Long id) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock movement not found with ID: " + id));
        return mapToResponse(movement);
    }

    /**
     * Validate request before processing
     */
    private void validateRequest(CreateStockMovementRequest request) {
        if (request.getType() == null) {
            throw new IllegalArgumentException("Movement type (IN/OUT) is required");
        }
        if (request.getReference() == null) {
            throw new IllegalArgumentException("Reference is required");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one item is required");
        }

        for (StockMovementItemRequest item : request.getItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("Product ID is required for all items");
            }
            if (item.getQuantity() == null || item.getQuantity().signum() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
        }
    }

    /**
     * Map StockMovement entity to response DTO
     */
    private StockMovementResponse mapToResponse(StockMovement movement) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .type(movement.getType())
                .reference(movement.getReference())
                .notes(movement.getNotes())
                .createdBy(movement.getCreatedBy())
                .createdAt(movement.getCreatedAt())
                .items(movement.getItems() != null ? 
                        movement.getItems().stream()
                            .map(this::mapItemToResponse)
                            .collect(Collectors.toList()) 
                        : null)
                .build();
    }

    /**
     * Map StockMovementItem to response DTO
     */
    private StockMovementItemResponse mapItemToResponse(StockMovementItem item) {
        return StockMovementItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getProductId())
                .productName(item.getProduct().getName())
                .sku(item.getProduct().getSku())
                .quantity(item.getQuantity())
                .reference(item.getReference())
                .build();
    }
}
