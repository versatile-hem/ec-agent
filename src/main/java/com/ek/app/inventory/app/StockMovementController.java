package com.ek.app.inventory.app;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.inventory.app.dto.CreateStockMovementRequest;
import com.ek.app.inventory.app.dto.StockMovementPageResponse;
import com.ek.app.inventory.app.dto.StockMovementResponse;
import com.ek.app.inventory.domain.StockMovementService;
import com.ek.app.inventory.infra.db.StockMovement.StockMovementType;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/stock-movements")
@Validated
@RequiredArgsConstructor
@Slf4j
public class StockMovementController {

    private final StockMovementService stockMovementService;

    /**
     * 1️⃣ CREATE STOCK MOVEMENT
     * POST /api/stock-movements
     * 
     * Request:
     * {
     *   "type": "IN",
     *   "reference": "MANUAL",
     *   "notes": "optional",
     *   "items": [
     *     {
     *       "productId": 1,
     *       "quantity": 10
     *     }
     *   ]
     * }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<StockMovementResponse> createStockMovement(
            @Valid @RequestBody CreateStockMovementRequest request) {
        
        log.info("Received request to create stock movement of type: {}", request.getType());
        
        String userId = getCurrentUserId();
        StockMovementResponse response = stockMovementService.createStockMovement(request, userId);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 2️⃣ GET STOCK MOVEMENTS
     * GET /api/stock-movements
     * 
     * Query Parameters:
     * - type: IN or OUT
     * - startDate: yyyy-MM-dd
     * - endDate: yyyy-MM-dd
     * - page: 0-based page number (default: 0)
     * - size: page size (default: 20)
     */
    @GetMapping
    public ResponseEntity<StockMovementPageResponse> getStockMovements(
            @RequestParam(required = false) StockMovementType type,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Fetching stock movements - type: {}, startDate: {}, endDate: {}, page: {}, size: {}", 
                type, startDate, endDate, page, size);
        
        StockMovementPageResponse response = stockMovementService.getStockMovements(
                type, startDate, endDate, page, size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 3️⃣ GET PRODUCT STOCK (for GET /api/products/{id})
     * This endpoint is updated in ProductController to include availableStock
     * 
     * GET /api/products/{id}
     * Response includes:
     * {
     *   "id": 1,
     *   "name": "Product A",
     *   "sku": "SKU001",
     *   "availableStock": 120
     * }
     */

    /**
     * Get single stock movement by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockMovementResponse> getStockMovementById(@PathVariable Long id) {
        log.info("Fetching stock movement with ID: {}", id);
        StockMovementResponse response = stockMovementService.getStockMovementById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user ID
     */
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "SYSTEM";
    }
}
