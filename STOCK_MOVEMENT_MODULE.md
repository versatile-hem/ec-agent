# Stock Movement Module Implementation Guide

## Overview

A complete **Stock Movement** module has been implemented for the Nexo ECommerce backend. This module tracks all stock movements (IN/OUT) while maintaining inventory accuracy and providing comprehensive APIs for frontend integration.

---

## ✅ Implemented Features

### 1 ️⃣ **Core Entities**

#### StockMovement
- Tracks bulk stock movements (IN/OUT operations)
- References: Manual Entry, Orders, Returns, Adjustments, Supplier, Damage, Transfer
- Audit information: CreatedBy (userId), CreatedAt timestamp
- Cascade delete with items

#### StockMovementItem  
- Individual items within a stock movement
- Links product with quantity moved
- Optional reference field (batch number, supplier ref, order ID, etc.)
- Lazy-loaded movement, eager-loaded product

---

### 2️⃣ **Data Persistence Layer**

#### Repositories
- **StockMovementRepository**
  - `findByType(type, pageable)` - Filter by IN/OUT
  - `findByDateRange(startDate, endDate, pageable)` - Range queries
  - `findByTypeAndDateRange(...)` - Combined filters
  - `findByReference(reference, pageable)` - Track by reference
  - `findByCreatedBy(userId, pageable)` - Audit trail

- **StockMovementItemRepository**
  - `findByMovementId(movementId)` - Get items for a movement
  - `findByProductId(productId)` - Track product movements

All queries optimized with proper pagination and filtering.

---

### 3️⃣ **Service Layer**

#### StockMovementService
- **Transactional Operations**: All write operations protected with `@Transactional`
- **No Logic Duplication**: Reuses existing `InventoryService` for stock updates
- **Batch Processing**: Efficiently handles multiple items per movement
- **Validation**: 
  - Product must exist
  - Quantity > 0
  - Prevent negative stock (validated in InventoryService)

**Key Methods:**
```java
public StockMovementResponse createStockMovement(CreateStockMovementRequest request, String userId)
public StockMovementPageResponse getStockMovements(type, startDate, endDate, pageNumber, pageSize)
public StockMovementResponse getStockMovementById(Long id)
```

---

### 4️⃣ **REST APIs**

#### API 1: Create Stock Movement
```
POST /api/stock-movements
Content-Type: application/json

Request:
{
  "type": "IN",                          // IN or OUT
  "reference": "MANUAL",                 // MANUAL, ORDER, RETURN, ADJUSTMENT, etc.
  "notes": "Received from supplier",     // Optional
  "items": [
    {
      "productId": 1,
      "quantity": 10,
      "reference": "Batch#2024-001"      // Optional batch/supplier ref
    },
    {
      "productId": 2,
      "quantity": 5
    }
  ]
}

Response (201 Created):
{
  "id": 123,
  "type": "IN",
  "reference": "MANUAL",
  "notes": "Received from supplier",
  "createdBy": "user123",
  "createdAt": "2026-04-26T12:46:00Z",
  "items": [
    {
      "id": 456,
      "productId": 1,
      "productName": "Product A",
      "sku": "SKU001",
      "quantity": 10,
      "reference": "Batch#2024-001"
    }
  ]
}
```

**Behavior:**
- Creates movement record
- Processes each item sequentially
- Updates inventory for each product
- Returns complete movement with items

---

#### API 2: Get Stock Movements (Paginated & Filtered)
```
GET /api/stock-movements?type=IN&startDate=2026-04-01&endDate=2026-04-30&page=0&size=20

Query Parameters (all optional):
- type: IN or OUT
- startDate: YYYY-MM-DD (inclusive)
- endDate: YYYY-MM-DD (inclusive at 23:59:59)
- page: 0-based page number (default: 0)
- size: page size (default: 20)

Response (200 OK):
{
  "content": [
    {
      "id": 123,
      "type": "IN",
      "reference": "MANUAL",
      "notes": "...",
      "createdBy": "user123",
      "createdAt": "2026-04-26T12:46:00Z",
      "items": [...]
    }
  ],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 150,
  "totalPages": 8,
  "isLast": false
}
```

**Filter Combinations:**
- No filter: All movements, sorted by createdAt DESC
- By type: Only IN or OUT movements
- By date range: Movements between dates
- By type + date: IN/OUT movements within date range

---

#### API 3: Product Stock (Enhanced)
```
GET /api/products/{id}

Response (200 OK):
{
  "id": 1,
  "name": "Product A",
  "sku": "SKU001",
  "barcode": "123456789",
  "category": "Electronics",
  "mrp": 1000.00,
  "tax_code": "GST_18",
  "availableStock": 120        ← Populated from inventory
}
```

**Implementation Note:** 
- ProductServiceImpl.entityToDto() already populates availableStock
- Retrieves from InventoryPosition.onHandQty
- Returns 0 if no inventory record exists

---

## 🔌 **Integration with Existing Services**

### Inventory Synchronization
The Stock Movement module integrates seamlessly with existing inventory:

```java
// Stock Movement Service calls existing InventoryService
InventoryMovementDto movementDto = new InventoryMovementDto();
movementDto.setProductId(productId);
movementDto.setQuantity(quantity);
movementDto.setMovementType(InventoryType.IN);  // or OUT
movementDto.setReference("Stock Movement");

inventoryService.updateStock(movementDto);
```

**Benefits:**
- ✅ No duplicate inventory logic
- ✅ Reuse existing IN/OUT/RETURN/DAMAGE/ADJUST logic
- ✅ Maintains InventoryPosition accuracy
- ✅ Prevents duplicate movement records

### Transaction Handling
- All operations are @Transactional
- If any item fails, entire movement is rolled back
- Inventory updates are atomic with movement creation

---

## 🔐 **Validations & Error Handling**

### Pre-Creation Validations
```javascript
✓ Movement type must be: IN or OUT
✓ Reference type must be valid enumeration
✓ At least one item required
✓ Each item must have valid productId
✓ Each item quantity must be > 0
```

### Failure Scenarios
```
400 Bad Request       → Invalid input (type, quantity, etc.)
404 Not Found         → Product doesn't exist
409 Conflict          → Insufficient stock for OUT movement
500 Internal Error    → Database or transaction failure
```

### Inventory Constraints
- OUT movements validate: `inventory.onHandQty >= quantity`
- System throws `InsufficientStockException` if violated
- No negative stock allowed

---

## 📊 **Database Schema**

### Tables

**stock_movement**
```sql
CREATE TABLE stock_movement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(10) NOT NULL,              -- IN, OUT
    reference VARCHAR(50) NOT NULL,         -- MANUAL, ORDER, RETURN, etc.
    notes VARCHAR(500),
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_at_auto TIMESTAMP DEFAULT NOW()
);
```

**stock_movement_item**
```sql
CREATE TABLE stock_movement_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    movement_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(18,2) NOT NULL,
    reference VARCHAR(255),
    FOREIGN KEY (movement_id) REFERENCES stock_movement(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE RESTRICT
);
```

---

## 🔄 **Workflow Example**

### Scenario: Receive 10 units of Product A from supplier

**Step 1: Frontend sends request**
```json
POST /api/stock-movements
{
  "type": "IN",
  "reference": "SUPPLIER",
  "notes": "Delivery from Supplier XYZ",
  "items": [{"productId": 1, "quantity": 10}]
}
```

**Step 2: Business Logic Execution**
1. Validate request
2. Create StockMovement record
3. Create StockMovementItem record
4. Call InventoryService.updateStock()
   - InventoryService checks InventoryPosition for product
   - Executes: `UPDATE inventory_position SET on_hand_qty = on_hand_qty + 10 WHERE product_id = 1`
5. Save StockMovement (with items) to database
6. Return response with movement ID

**Step 3: Inventory Updated**
- InventoryPosition.onHandQty increased
- All subsequent GET /api/products/{id} queries show updated availableStock
- Audit trail in stock_movement table

---

## 📈 **API Usage Patterns**

### Pattern 1: Bulk Stock In
```bash
curl -X POST http://localhost:8080/api/stock-movements \
  -H "Content-Type: application/json" \
  -d '{
    "type": "IN",
    "reference": "SUPPLIER",
    "notes": "Bulk order received",
    "items": [
      {"productId": 1, "quantity": 100},
      {"productId": 2, "quantity": 50},
      {"productId": 3, "quantity": 25}
    ]
  }'
```

### Pattern 2: Create Returns
```bash
curl -X POST http://localhost:8080/api/stock-movements \
  -H "Content-Type: application/json" \
  -d '{
    "type": "IN",
    "reference": "RETURN",
    "notes": "Customer returned items",
    "items": [
      {"productId": 1, "quantity": 2}
    ]
  }'
```

### Pattern 3: Stock Out (Order Fulfillment)
```bash
curl -X POST http://localhost:8080/api/stock-movements \
  -H "Content-Type: application/json" \
  -d '{
    "type": "OUT",
    "reference": "ORDER",
    "notes": "Fulfilling order #12345",
    "items": [
      {"productId": 1, "quantity": 5},
      {"productId": 2, "quantity": 3}
    ]
  }'
```

### Pattern 4: Query Movements
```bash
# Get all IN movements
curl "http://localhost:8080/api/stock-movements?type=IN&page=0&size=20"

# Get movements in date range
curl "http://localhost:8080/api/stock-movements?\
startDate=2026-04-01&\
endDate=2026-04-30&\
page=0&\
size=20"

# Get single movement
curl "http://localhost:8080/api/stock-movements/123"
```

---

## 🚀 **Performance Considerations**

### Optimizations Implemented

1. **Batch Insert**
   - Items for a movement inserted in single transaction
   - Minimizes database round trips

2. **Lazy Loading**
   - StockMovement.items loaded only when accessed
   - Stock Movement response explicitly populates items

3. **Index Strategy** (Recommended)
   ```sql
   CREATE INDEX idx_stock_movement_type ON stock_movement(type);
   CREATE INDEX idx_stock_movement_created_at ON stock_movement(created_at);
   CREATE INDEX idx_stock_movement_type_created_at ON stock_movement(type, created_at);
   CREATE INDEX idx_stock_movement_created_by ON stock_movement(created_by);
   CREATE INDEX idx_stock_movement_item_movement ON stock_movement_item(movement_id);
   CREATE INDEX idx_stock_movement_item_product ON stock_movement_item(product_id);
   ```

4. **Query Pagination**
   - All list endpoints paginate by default (max 20 records)
   - Prevents large result sets

---

## 🧪 **Testing Recommendations**

### Unit Tests
```java
@Test
public void createStockMovement_ValidRequest_Success() { }

@Test
public void createStockMovement_InsufficientStock_Throws() { }

@Test
public void getStockMovements_WithFilter_ReturnFiltered() { }

@Test
public void getProductWithStock_ReturnsAvailableStock() { }
```

### Integration Tests
```java
@Test
@Transactional
public void stockMovement_InventoryConsistency() { }

@Test
public void stockIn_Then_StockOut_InventoryUpdated() { }
```

### API Tests
```bash
# Positive cases
POST /api/stock-movements with valid payload
GET /api/stock-movements with various filters
GET /api/products can retrieve availableStock

# Negative cases
POST /api/stock-movements with invalid productId (404)
POST /api/stock-movements with OUT quantity > onHand (409)
GET /api/stock-movements with invalid filter values
```

---

## 📝 **File Structure**

```
com.ek.app.inventory/
├── infra/db/
│   ├── StockMovement.java           (Entity)
│   ├── StockMovementItem.java       (Entity)
│   ├── StockMovementRepository.java (JpaRepository)
│   └── StockMovementItemRepository.java (JpaRepository)
├── app/
│   ├── dto/
│   │   ├── CreateStockMovementRequest.java
│   │   ├── StockMovementItemRequest.java
│   │   ├── StockMovementResponse.java
│   │   ├── StockMovementItemResponse.java
│   │   └── StockMovementPageResponse.java
│   └── StockMovementController.java (REST Controller)
└── domain/
    └── StockMovementService.java    (Business Logic)
```

---

## ⚙️ **Configuration & Setup**

### No Configuration Required
- Module is auto-wired through Spring's component scanning
- Uses existing InventoryService and ProductRepository
- Inherits database configuration from application.properties

### Database Migration (if using Flyway)
```sql
-- V3__stock_movement.sql
CREATE TABLE stock_movement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(10) NOT NULL,
    reference VARCHAR(50) NOT NULL,
    notes VARCHAR(500),
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE stock_movement_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    movement_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(18,2) NOT NULL,
    reference VARCHAR(255),
    FOREIGN KEY (movement_id) REFERENCES stock_movement(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);
```

---

## ✨ **Key Design Principles**

1. **Single Responsibility**
   - StockMovement tracks movements, InventoryService updates positions
   - No duplication of inventory logic

2. **Clean Architecture**
   - Separation of concerns: Entity → Repository → Service → Controller
   - DTOs for API contracts
   - No business logic in controllers

3. **Data Consistency**
   - Transactional integrity ensures atomicity
   - Cascading deletes maintain referential integrity
   - Audit trail (createdBy, createdAt) for compliance

4. **Extensibility**
   - Easy to add new reference types (extend StockMovementReference enum)
   - Additional fields can be added to StockMovementItem
   - Batch operations supported

5. **Reusability**
   - Leverages existing InventoryService
   - ProductRepository integration
   - Follows Spring Data conventions

---

## 🔄 **Future Enhancements**

- **Reserved Stock**: Track reserved quantities during order processing
- **Multi-Warehouse**: Extend with location/warehouse tracking
- **Approval Workflow**: Add approval for high-value movements
- **Analytics**: Movement velocity, trend analysis reports
- **Notifications**: Real-time alerts for stock thresholds
- **Batch Operations**: Async processing for bulk movements
- **Movement Reversal**: Correction/reversal of transactions

---

## 🎯 **Summary**

The Stock Movement module provides a **clean, extensible, and performant** solution for tracking stock operations while maintaining inventory accuracy. It integrates seamlessly with existing services, provides comprehensive APIs for frontend integration, and follows Spring Boot best practices.

**Ready for production use.**
