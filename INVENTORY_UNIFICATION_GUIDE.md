# Inventory System Unification - Technical Implementation Guide

## Quick Reference

### Unified Service Methods (entry points)

```java
// Purchase/Inward Operations
stockMovementService.recordStockIn(
    productId: Long,
    quantity: BigDecimal,
    supplier: String,
    batchNumber: String,
    movementTime: LocalDateTime,
    userId: String
) → InventoryBalanceResponseDto

// Sales/Outward Operations
stockMovementService.recordStockOut(
    productId: Long,
    quantity: BigDecimal,
    courier: String,
    channel: String,
    movementTime: LocalDateTime,
    userId: String
) → InventoryBalanceResponseDto

// Daily Operations (flexible for orders/returns/adjustments)
stockMovementService.recordDailyOperation(
    productId: Long,
    quantity: BigDecimal,
    operationType: String,  // "ORDER", "RETURN", "ADJUSTMENT"
    courier: String,
    channel: String,
    movementTime: LocalDateTime,
    userId: String
) → InventoryBalanceResponseDto
```

---

## Architecture

### Data Model

```
StockMovement (Master)
├── id: Long (PK)
├── type: StockMovementType (IN/OUT)
├── reference: StockMovementReference (MANUAL/ORDER/RETURN/SUPPLIER/etc.)
├── notes: String
├── createdBy: String (userId audit)
├── createdAt: LocalDateTime (audit)
└── items: List<StockMovementItem> (1:N)
    ├── id: Long
    ├── movement: StockMovement (FK)
    ├── product: Product (FK)
    ├── quantity: BigDecimal
    └── reference: String (batch#, order#, etc.)

InventoryPosition (Computed State)
├── id: Long (PK)
├── product: Product (FK, UNIQUE)
├── onHandQty: BigDecimal (total available)
├── reservedQty: BigDecimal
├── avgCost: BigDecimal
└── lastModifiedOn: LocalDateTime

Deprecated (No longer used):
❌ InventoryMovement (use StockMovement instead)
❌ InventoryMovementRepository
❌ InventoryService (use StockMovementService)
```

### Data Flow

```
API Request
  ↓
InventoryOperationsController
  ↓
StockMovementService.record{StockIn|StockOut|DailyOperation}()
  ├─→ Validate product exists
  ├─→ Validate quantity > 0
  ├─→ Check stock (for OUT operations)
  ├─→ Create StockMovement + StockMovementItem
  ├─→ Update InventoryPosition
  └─→ Return InventoryBalanceResponseDto

Success Criteria:
✓ Both tables updated (stock_movement + inventory_position)
✓ Quantity matches after operation
✓ All validations pass
✓ Transaction atomic (all-or-nothing)

Failure Scenarios:
✗ Product not found → ResourceNotFoundException
✗ Quantity ≤ 0 → IllegalArgumentException
✗ Insufficient stock (OUT) → InsufficientStockException
✗ DB error → Rollback, error propagated
```

---

## API Endpoints

### 1. Stock In (Purchase/Inward)

**POST /api/stock-in**

Request:
```json
{
  "requests": [
    {
      "productId": 1,
      "quantity": 50.00,
      "supplier": "XYZ Corp",
      "batchNumber": "BATCH-2026-001",
      "unit": "nos",
      "movementTime": "2026-04-26T10:00:00"
    }
  ]
}
```

Response: `List<InventoryBalanceResponseDto>`
```json
[
  {
    "productId": 1,
    "quantity": 750.00
  }
]
```

Flow:
1. For each request, call `stockMovementService.recordStockIn()`
2. Creates StockMovement with type=IN, reference=SUPPLIER
3. Updates inventory_position (adds quantity)
4. Returns final balance

---

### 2. Daily Operations (Orders/Returns)

**POST /api/daily-operations**

Request:
```json
{
  "productId": 2,
  "quantity": 10.00,
  "type": "ORDER",
  "courier": "Flipkart",
  "channel": "FLIPKART",
  "unit": "nos",
  "movementTime": "2026-04-26T15:30:00"
}
```

Response: `InventoryBalanceResponseDto`
```json
{
  "productId": 2,
  "quantity": 290.00
}
```

Flow:
1. Call `stockMovementService.recordDailyOperation(type="ORDER")`
2. type="ORDER" → StockMovementType.OUT (subtract qty)
3. type="RETURN" → StockMovementType.IN (add qty)
4. Validates sufficient stock if OUT
5. Returns final balance

---

### 3. Batch End-of-Day Operations

**POST /api/end-of-day-operations**

Request:
```json
{
  "notes": "Daily reconciliation",
  "operations": [
    {
      "productId": 1,
      "quantity": 5,
      "type": "ORDER",
      "courier": "Flipkart",
      "unit": "nos",
      "movementTime": "2026-04-26T18:00:00"
    },
    {
      "productId": 2,
      "quantity": 3,
      "type": "RETURN",
      "courier": "Amazon",
      "unit": "nos",
      "movementTime": "2026-04-26T18:15:00"
    }
  ]
}
```

Response: `EndOfDayBatchResponseDto`
```json
{
  "processedAt": "2026-04-26T22:19:33.488081",
  "totalOperationsProcessed": 2,
  "successfulOperations": 1,
  "failedOperations": 1,
  "status": "PARTIAL_SUCCESS",
  "results": [
    {
      "productId": 1,
      "productName": "Sample Item",
      "quantity": 5,
      "operationType": "ORDER",
      "success": true,
      "processedTime": "2026-04-26T22:19:33.568072"
    },
    {
      "productId": 2,
      "success": false,
      "errorMessage": "Insufficient stock for product: 2. Available: 50, Required: 100"
    }
  ],
  "finalPositions": [
    {
      "productId": 1,
      "productName": "Sample Item",
      "previousBalance": 100.00,
      "adjustmentQuantity": 5,
      "adjustmentType": "OUT",
      "newBalance": 95.00
    }
  ]
}
```

Flow:
1. Iterate through operations array
2. For each: call `stockMovementService.recordDailyOperation()`
3. Track success/failure per item
4. Return detailed response with before/after positions
5. Status = SUCCESS | PARTIAL_SUCCESS | FAILED

---

### 4. Get Current Inventory

**GET /api/inventory?productId=1** or **GET /api/inventory/1**

Response:
```json
{
  "productId": 1,
  "quantity": 750.00
}
```

Flow:
1. Query `InventoryPosition` directly
2. Return on_hand_qty (current available)
3. No movement records created

---

## Integration with Batch Operations

The unified service powers the batch endpoint:

```java
@PostMapping("/end-of-day-operations")
public EndOfDayBatchResponseDto processEndOfDayOperations(
        @Valid @RequestBody EndOfDayBatchRequestDto batchRequest) {
    
    String userId = getCurrentUserId();
    List<OperationResultDto> results = new ArrayList<>();
    List<InventoryFinalPositionDto> finalPositions = new ArrayList<>();
    
    // Process each operation atomically
    for (DailyOperationRequestDto operation : batchRequest.getOperations()) {
        try {
            // Call unified service
            InventoryBalanceResponseDto response = 
                stockMovementService.recordDailyOperation(
                    operation.getProductId(),
                    operation.getQuantity(),
                    operation.getType().toString(),
                    operation.getCourier(),
                    operation.getChannel().toString(),
                    operation.getMovementTime() != null ? 
                        operation.getMovementTime() : LocalDateTime.now(),
                    userId
                );
            
            // Track result
            results.add(success(...));
            finalPositions.add(positionAfter(...));
            
        } catch (InsufficientStockException e) {
            // Partial failure - continue processing
            results.add(failure(e.getMessage()));
        }
    }
    
    return EndOfDayBatchResponseDto.builder()
            .processedAt(LocalDateTime.now())
            .successfulOperations(successCount)
            .failedOperations(failCount)
            .status(status)
            .results(results)
            .finalPositions(finalPositions)
            .build();
}
```

**Key Points**:
- Each operation is processed independently
- Failures don't stop other operations (partial success)
- Before/after positions captured for audit
- Single userId captured for all batch operations
- Result includes detailed per-item status

---

## Exception Handling

### ResourceNotFoundException
**When**: Product doesn't exist
**Response**: HTTP 404
**Handling**: Thrown by recordStockIn/Out/DailyOperation
```java
throw new ResourceNotFoundException("Product not found with ID: " + productId);
```

### IllegalArgumentException
**When**: Quantity ≤ 0 or invalid input
**Response**: HTTP 400
**Handling**: Validate at DTO level and service
```java
throw new IllegalArgumentException("Quantity must be greater than zero");
```

### InsufficientStockException
**When**: Trying to remove more than available
**Response**: HTTP 400
**Handling**: Checked in updateInventoryPosition() for OUT operations
```java
throw new InsufficientStockException(
    "Insufficient stock for product: " + productId + 
    ". Available: " + currentQty + ", Required: " + quantity
);
```

### Batch Handling
- Individual item failures caught in try-catch
- Batch continues processing
- Response includes detailed error messages per item
- Overall status reflects: SUCCESS | PARTIAL_SUCCESS | FAILED

---

## Transactional Guarantees

### Method-Level Atomicity

```java
@Transactional  // ← All-or-nothing boundary
public InventoryBalanceResponseDto recordStockIn(...) {
    // 1. Create StockMovement
    StockMovement movement = stockMovementRepository.save(movement);
    
    // 2. Create StockMovementItem
    stockMovementItemRepository.save(item);
    
    // 3. Update InventoryPosition
    inventoryPositionRepository.addOnHandQty(productId, quantity);
    
    // If ANY step fails → entire transaction rolls back
    // Both stock_movement and inventory_position stay consistent
}
```

### Batch-Level Transparency

- Each operation in batch is independent
- If operation A fails, operation B still processes
- Database operations are still atomic per operation
- No cross-operation transaction (by design for flexibility)

### Consistency Model

```
Strong Consistency per Operation:
✓ Stock movement created XOR inventory not updated = NEVER
✓ Inventory updated XOR quantity mismatch = NEVER
✓ Exception thrown XOR data committed = NEVER

Eventual Consistency in Batch:
? Operation 1 succeeds, Operation 2 fails
✓ Both outcomes visible to client immediately
✓ No silent failures (detailed error per item)
```

---

## Database Queries

### Movement Records

```sql
-- All movements for product
SELECT * FROM stock_movement m 
JOIN stock_movement_item i ON m.id = i.movement_id
WHERE i.product_id = ? 
ORDER BY m.created_at DESC;

-- Movements in date range
SELECT * FROM stock_movement 
WHERE created_at BETWEEN ? AND ?
AND type = 'IN' or type = 'OUT';

-- Movements by reference
SELECT * FROM stock_movement
WHERE reference = 'ORDER'
ORDER BY created_at DESC;
```

### Inventory Position

```sql
-- Current balance
SELECT on_hand_qty, reserved_qty, avg_cost 
FROM inventory_position 
WHERE product_id = ?;

-- Products with low stock
SELECT p.name, ip.on_hand_qty
FROM inventory_position ip
JOIN product p ON ip.product_id = p.product_id
WHERE ip.on_hand_qty < 10
ORDER BY ip.on_hand_qty ASC;

-- Total value
SELECT SUM(ip.on_hand_qty * ip.avg_cost) 
FROM inventory_position ip;
```

---

## Error Scenarios & Handling

### Scenario 1: Insufficient Stock
```
Request: recordStockOut(productId=1, quantity=100)
Current: on_hand_qty = 50
Result: InsufficientStockException thrown → HTTP 400
Body:   {"message": "Insufficient stock for product: 1..."}
```

### Scenario 2: Invalid Product
```
Request: recordStockIn(productId=99999, ...)
Result: ResourceNotFoundException thrown → HTTP 404
Body:   {"message": "Product not found with ID: 99999"}
```

### Scenario 3: Batch with Partial Failure
```
Request 1: recordDailyOperation(productId=1, qty=5) → SUCCESS
Request 2: recordDailyOperation(productId=99, qty=10) → FAIL (not found)
Request 3: recordDailyOperation(productId=2, qty=100) → FAIL (insufficient)

Response: HTTP 201 (overall success)
Status: "PARTIAL_SUCCESS"
Results: [
  {success: true, product: 1},
  {success: false, error: "Product not found"},
  {success: false, error: "Insufficient stock"}
]
```

---

## Migration from Old System

### If still using InventoryService directly:

**Old Code**:
```java
InventoryMovementDto movement = new InventoryMovementDto();
movement.setProductId(1);
movement.setQuantity(new BigDecimal("50"));
movement.setMovementType(InventoryType.IN);
inventoryService.updateStock(movement);
```

**New Code**:
```java
stockMovementService.recordStockIn(
    1L,
    new BigDecimal("50"),
    "Supplier Name",
    "BATCH-001",
    LocalDateTime.now(),
    userId
);
```

**Benefits**:
- Single method call (no DTO construction needed)
- Automatic user tracking
- Consistent naming
- Better error handling
- Audit trail

---

## Performance Considerations

### Indexes (Recommended)

```sql
-- Fast product lookups
CREATE INDEX idx_stock_movement_item_product 
ON stock_movement_item(product_id);

-- Fast date range queries
CREATE INDEX idx_stock_movement_created 
ON stock_movement(created_at DESC);

-- Fast inventory lookups
CREATE INDEX idx_inventory_position_product 
ON inventory_position(product_id);

-- Reference type filtering
CREATE INDEX idx_stock_movement_reference 
ON stock_movement(reference);
```

### Query Optimization

- Eager load Product in StockMovementItem (already configured)
- Batch operations can use connection pooling
- InventoryPosition uses direct update queries (no SELECT-UPDATE)

### Typical Response Times

- recordStockIn: 10-50ms (2 inserts + 1 update)
- recordStockOut: 15-60ms (2 inserts + 2 queries + 1 update + validation)
- Batch 100 items: 1-2 seconds (parallel processing possible)

---

## Testing

### Unit Test Example

```java
@Test
public void testRecordStockIn() {
    // Given
    Product product = createTestProduct(id=1);
    
    // When
    InventoryBalanceResponseDto result = 
        stockMovementService.recordStockIn(
            1L,
            new BigDecimal("100"),
            "TestSupplier",
            "BATCH-001",
            LocalDateTime.now(),
            "testuser"
        );
    
    // Then
    assertEquals(new BigDecimal("100"), result.getQuantity());
    
    StockMovement movement = stockMovementRepository.findById(1L).get();
    assertEquals(StockMovementType.IN, movement.getType());
    assertEquals(1, movement.getItems().size());
    
    InventoryPosition position = 
        inventoryPositionRepository.findByProduct(product).get();
    assertEquals(new BigDecimal("100"), position.getOnHandQty());
}
```

### Integration Test

```java
@Test
public void testBatchEndOfDay() {
    // Given: 2 operations
    EndOfDayBatchRequestDto batch = new EndOfDayBatchRequestDto();
    batch.setOperations(List.of(
        operation(productId=1, qty=5, type="ORDER"),     // OUT
        operation(productId=2, qty=3, type="RETURN")     // IN
    ));
    
    // When
    EndOfDayBatchResponseDto result = 
        controller.processEndOfDayOperations(batch);
    
    // Then
    assertEquals(2, result.getTotalOperationsProcessed());
    assertEquals(2, result.getSuccessfulOperations());
    assertEquals("SUCCESS", result.getStatus());
    assertEquals(2, result.getFinalPositions().size());
}
```

---

## FAQ

**Q: Why keep InventoryMovement table if not using it?**  
A: Backward compatibility. Can be removed after confirming no external code depends on it.

**Q: How do I track which user made each movement?**  
A: Via `StockMovement.createdBy` field. Pass userId to service methods.

**Q: What if I need custom fields in stock movements?**  
A: Add to `StockMovement.notes` field or extend StockMovementItem.

**Q: Can I run movements in parallel?**  
A: Yes, each @Transactional method is independent. DB locks handle conflicts.

**Q: How do I query movement history?**  
A: Query `stock_movement` + `stock_movement_item` tables.

**Q: What about reserved stock?**  
A: Tracked in `inventory_position.reserved_qty`. Not updated by default methods (future enhancement).

**Q: Can I adjust quantities manually?**  
A: Use `recordDailyOperation(type="ADJUSTMENT")` to create audit trail.

---

## Conclusion

The unified inventory system provides:
- ✅ Single API for all movements
- ✅ Atomic operations
- ✅ Comprehensive audit trail
- ✅ Easy error handling
- ✅ Batch processing support
- ✅ Data consistency guarantees

For questions or updates, refer to StockMovementService javadocs or this guide.
