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

import com.ek.app.inventory.app.dto.StockMovementItemResponse;
import com.ek.app.inventory.app.dto.StockMovementPageResponse;
import com.ek.app.inventory.app.dto.StockMovementResponse;
import com.ek.app.inventory.infra.db.InventoryMovement;
import com.ek.app.inventory.infra.db.InventoryMovementRepository;
import com.ek.app.inventory.infra.db.StockMovementReference;
import com.ek.app.inventory.infra.db.StockMovementType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to query inventory_movement table and return results in stock_movement format.
 * This bridges the legacy inventory_movement table with the stock_movement API.
 * 
 * Mapping:
 * - InventoryMovement.id → StockMovementResponse.id
 * - InventoryMovement.movementType → StockMovementResponse.type (IN/OUT; RETURN→IN, DAMAGE→OUT)
 * - InventoryMovement.reference (String) → StockMovementResponse.reference (enum)
 * - InventoryMovement → StockMovementResponse.items (single item per movement)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryMovementStockMovementService {

    private final InventoryMovementRepository inventoryMovementRepository;

    /**
     * Get inventory movements with optional filtering
     * Maps from inventory_movement table in stock_movement API format
     */
    @Transactional(readOnly = true)
    public StockMovementPageResponse getStockMovements(
            StockMovementType type,
            LocalDate startDate,
            LocalDate endDate,
            int pageNumber,
            int pageSize) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNumber, pageSize);
        Page<InventoryMovement> page;

        // Convert StockMovementType (IN/OUT) back to InventoryType for query
        InventoryType inventoryType = type != null ? convertToInventoryType(type) : null;

        if (inventoryType != null && startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            page = inventoryMovementRepository.findByMovementTypeAndMovementTimeRange(
                    inventoryType, startDateTime, endDateTime, pageable);
        } else if (inventoryType != null) {
            page = inventoryMovementRepository.findByMovementType(inventoryType, pageable);
        } else if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            page = inventoryMovementRepository.findByMovementTimeRange(
                    startDateTime, endDateTime, pageable);
        } else {
            page = inventoryMovementRepository.findAllOrderByMovementTimeDesc(pageable);
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
     * Get single inventory movement by ID
     */
    @Transactional(readOnly = true)
    public StockMovementResponse getStockMovementById(Long id) {
        InventoryMovement movement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock movement not found with ID: " + id));
        return mapToResponse(movement);
    }

    /**
     * Convert InventoryType to StockMovementType
     * IN → IN
     * OUT → OUT
     * RETURN → IN (returns are inbound)
     * DAMAGE → OUT (damaged items are outbound)
     * ADJUST → IN (adjustments default to inbound)
     */
    private InventoryType convertToInventoryType(StockMovementType type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case IN:
                return InventoryType.IN;
            case OUT:
                return InventoryType.OUT;
            default:
                return null;
        }
    }

    /**
     * Convert StockMovementType to filter for inventory query
     * Since InventoryType has IN, OUT, RETURN, DAMAGE, ADJUST
     * we query for IN or OUT or appropriate subtypes
     */
    private List<InventoryType> getFilteredInventoryTypes(StockMovementType type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case IN:
                // IN movements: IN, RETURN, ADJUST
                return List.of(InventoryType.IN, InventoryType.RETURN, InventoryType.ADJUST);
            case OUT:
                // OUT movements: OUT, DAMAGE
                return List.of(InventoryType.OUT, InventoryType.DAMAGE);
            default:
                return null;
        }
    }

    /**
     * Map InventoryMovement entity to StockMovementResponse DTO
     * Each movement becomes a single item in the items array
     */
    private StockMovementResponse mapToResponse(InventoryMovement movement) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .type(mapInventoryTypeToStockMovementType(movement.getMovementType()))
                .reference(mapReferenceToEnum(movement.getReference(), movement.getMovementType()))
                .notes(movement.getReference()) // Use reference as notes for context
                .createdBy(movement.getCreatedBy())
                .createdAt(movement.getCreatedAt())
                .items(List.of(mapItemToResponse(movement)))
                .build();
    }

    /**
     * Map InventoryMovement to StockMovementItemResponse
     */
    private StockMovementItemResponse mapItemToResponse(InventoryMovement movement) {
        return StockMovementItemResponse.builder()
                .id(movement.getId()) // Use movement ID as item ID
                .productId(movement.getProduct().getProductId())
                .productName(movement.getProduct().getName())
                .sku(movement.getProduct().getSku())
                .quantity(movement.getQuantity())
                .reference(movement.getReference())
                .build();
    }

    /**
     * Convert InventoryType to StockMovementType
     */
    private StockMovementType mapInventoryTypeToStockMovementType(InventoryType type) {
        if (type == null) {
            return StockMovementType.IN;
        }
        switch (type) {
            case IN:
            case RETURN:
            case ADJUST:
                return StockMovementType.IN;
            case OUT:
            case DAMAGE:
                return StockMovementType.OUT;
            default:
                return StockMovementType.IN;
        }
    }

    /**
     * Map reference string to StockMovementReference enum
     * Intelligently guesses based on reference content
     */
    private StockMovementReference mapReferenceToEnum(String reference, InventoryType type) {
        if (reference == null || reference.isBlank()) {
            return StockMovementReference.MANUAL;
        }

        String ref = reference.toUpperCase();

        // Try exact matches
        if (ref.contains("ORDER")) return StockMovementReference.ORDER;
        if (ref.contains("RETURN")) return StockMovementReference.RETURN;
        if (ref.contains("ADJUSTMENT")) return StockMovementReference.ADJUSTMENT;
        if (ref.contains("SUPPLIER")) return StockMovementReference.SUPPLIER;
        if (ref.contains("DAMAGE")) return StockMovementReference.DAMAGE;
        if (ref.contains("TRANSFER")) return StockMovementReference.TRANSFER;
        if (ref.contains("MEESHO")) return StockMovementReference.MEESHO;
        if (ref.contains("FLIPKART")) return StockMovementReference.FLIPKART;

        // Fallback based on movement type
        switch (type) {
            case RETURN:
                return StockMovementReference.RETURN;
            case DAMAGE:
                return StockMovementReference.DAMAGE;
            case ADJUST:
                return StockMovementReference.ADJUSTMENT;
            default:
                return StockMovementReference.MANUAL;
        }
    }
}
