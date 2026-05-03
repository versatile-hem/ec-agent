package com.ek.app.inventory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ek.app.inventory.app.dto.CreateStockMovementRequest;
import com.ek.app.inventory.app.dto.StockMovementItemRequest;
import com.ek.app.productcatalog.infra.db.Product;
import com.ek.app.productcatalog.infra.db.ProductRepository;
import com.ek.app.inventory.infra.db.InventoryMovementRepository;
import com.ek.app.inventory.infra.db.InventoryPositionRepository;
import com.ek.app.inventory.infra.db.StockMovementType;
import com.ek.app.inventory.infra.db.StockMovementReference;

/**
 * Comprehensive integration test for inventory flow
 * Tests: Add Product -> Stock In -> Stock Out -> Daily Update -> Verify Stock
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Inventory Flow Integration Tests")
public class InventoryFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private InventoryPositionRepository inventoryPositionRepository;

    private Long productId;
    private String testProductSku = "TEST-SKU-" + System.currentTimeMillis();

    @BeforeEach
    public void setup() throws Exception {
        // Clean up test data - delete in correct order to respect foreign keys
        inventoryMovementRepository.deleteAll();
        inventoryPositionRepository.deleteAll();
        productRepository.deleteAll();

        // Create test product
        productId = createTestProduct();
    }

    /**
     * Helper: Create test product with unique SKU
     */
    private Long createTestProduct() {
        Product product = new Product();
        product.setName("Test Product");
        product.setProduct_title("Test Product for Integration Testing");
        // Generate unique SKU for each product to avoid constraint violation
        product.setSku(testProductSku + "-" + System.nanoTime());
        product.setActive(true);

        Product saved = productRepository.save(product);
        return saved.getProductId();
    }

    // ==================== MAIN FLOW TESTS ====================

    @Test
    @DisplayName("1️⃣ Stock In: Receive 100 units from supplier")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testStockIn_ReceiveFromSupplier() throws Exception {
        // Arrange
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.IN);
        request.setReference(StockMovementReference.SUPPLIER);
        request.setNotes("Initial stock from supplier");

        StockMovementItemRequest item = new StockMovementItemRequest();
        item.setProductId(productId);
        item.setQuantity(new BigDecimal("100"));
        item.setReference("Challan #12345");
        request.setItems(java.util.Arrays.asList(item));

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("IN"))
                .andExpect(jsonPath("$.reference").value("SUPPLIER"))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId").value(Math.toIntExact(productId)))
                .andExpect(jsonPath("$.items[0].quantity").value(100))
                .andReturn();

        // Verify product stock updated
        Thread.sleep(500); // Wait for async processing if any
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableStock").value(100));
    }

    @Test
    @DisplayName("2️⃣ Stock Out: Fulfill order of 30 units")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testStockOut_FulfillOrder() throws Exception {
        // Setup: First add 100 units
        addStockInSupplier(new BigDecimal("100"));

        // Arrange: Create OUT movement
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.OUT);
        request.setReference(StockMovementReference.OFFLINE);
        request.setNotes("Customer order fulfillment");

        StockMovementItemRequest item = new StockMovementItemRequest();
        item.setProductId(productId);
        item.setQuantity(new BigDecimal("30"));
        request.setItems(java.util.Arrays.asList(item));

        // Act & Assert
        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("OUT"))
                .andReturn();

        // Verify stock reduced to 70
        Thread.sleep(500);
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableStock").value(70));
    }

    @Test
    @DisplayName("3️⃣ Daily Update (Meesho): Sell 20 units via Meesho")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testDailyUpdate_MeeshoSale() throws Exception {
        // Setup: First add 100 units, then remove 30
        addStockInSupplier(new BigDecimal("100"));
        removeStockOut(new BigDecimal("30"));

        // Arrange: Create Meesho OUT movement
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.OUT);
        request.setReference(StockMovementReference.MEESHO);
        request.setNotes("Daily Meesho order fulfillment");

        StockMovementItemRequest item = new StockMovementItemRequest();
        item.setProductId(productId);
        item.setQuantity(new BigDecimal("20"));
        request.setItems(java.util.Arrays.asList(item));

        // Act & Assert
        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").value("MEESHO"))
                .andReturn();

        // Verify final stock = 50 (100 - 30 - 20)
        Thread.sleep(500);
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableStock").value(50));
    }

    @Test
    @DisplayName("4️⃣ Verify Final Stock: Check availableStock after all movements")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testVerifyFinalStock() throws Exception {
        // Setup complete flow: 100 in, 30 out, 20 out
        addStockInSupplier(new BigDecimal("100"));
        removeStockOut(new BigDecimal("30"));
        removeStockOut(new BigDecimal("20"));

        // Act & Verify
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableStock").value(50))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.sku").exists()); // SKU is unique per test run
    }

    // ==================== NEGATIVE TEST CASES ====================

    @Test
    @DisplayName("❌ Insufficient Stock: Try to remove more than available")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testInsufficientStock_ShouldFail() throws Exception {
        // Setup: Add only 50 units
        addStockInSupplier(new BigDecimal("50"));

        // Arrange: Try to remove 100 units
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.OUT);
        request.setReference(StockMovementReference.OFFLINE);
        request.setNotes("This should fail");

        StockMovementItemRequest item = new StockMovementItemRequest();
        item.setProductId(productId);
        item.setQuantity(new BigDecimal("100"));
        request.setItems(java.util.Arrays.asList(item));

        // Act & Assert
        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Insufficient")));
    }

    @Test
    @DisplayName("❌ Invalid Product: Try movement with non-existent product")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testInvalidProduct_ShouldFail() throws Exception {
        // Arrange: Use non-existent product ID
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.IN);
        request.setReference(StockMovementReference.SUPPLIER);

        StockMovementItemRequest item = new StockMovementItemRequest();
        item.setProductId(99999L);
        item.setQuantity(new BigDecimal("100"));
        request.setItems(java.util.Arrays.asList(item));

        // Act & Assert
        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found")));
    }

    @Test
    @DisplayName("❌ Zero Quantity: Try movement with zero quantity")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testZeroQuantity_ShouldFail() throws Exception {
        // Arrange
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.IN);
        request.setReference(StockMovementReference.SUPPLIER);

        StockMovementItemRequest item = new StockMovementItemRequest();
        item.setProductId(productId);
        item.setQuantity(new BigDecimal("0"));
        request.setItems(java.util.Arrays.asList(item));

        // Act & Assert
        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("❌ Negative Quantity: Try movement with negative quantity")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testNegativeQuantity_ShouldFail() throws Exception {
        // Arrange
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.IN);
        request.setReference(StockMovementReference.SUPPLIER);

        StockMovementItemRequest item = new StockMovementItemRequest();
        item.setProductId(productId);
        item.setQuantity(new BigDecimal("-50"));
        request.setItems(java.util.Arrays.asList(item));

        // Act & Assert
        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("❌ Missing Type: Request without type field")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testMissingType_ShouldFail() throws Exception {
        // Arrange: Create invalid request without type
        String jsonRequest = """
                {
                    "reference": "SUPPLIER",
                    "items": [
                        {
                            "productId": %d,
                            "quantity": 100
                        }
                    ]
                }
                """.formatted(productId);

        // Act & Assert
        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("❌ Empty Items: Request with empty items list")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testEmptyItems_ShouldFail() throws Exception {
        // Arrange
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.IN);
        request.setReference(StockMovementReference.SUPPLIER);
        request.setItems(java.util.Arrays.asList()); // Empty list

        // Act & Assert
        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== BULK ITEMS TEST ====================

    @Test
    @DisplayName("🔁 Bulk Items: Multiple products in single movement")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testBulkItems_MultipleProducts() throws Exception {
        // Setup: Create second product
        Long productId2 = createTestProduct();

        // Arrange: Create movement with 2 items
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.IN);
        request.setReference(StockMovementReference.SUPPLIER);
        request.setNotes("Bulk receiving from supplier");

        StockMovementItemRequest item1 = new StockMovementItemRequest();
        item1.setProductId(productId);
        item1.setQuantity(new BigDecimal("100"));

        StockMovementItemRequest item2 = new StockMovementItemRequest();
        item2.setProductId(productId2);
        item2.setQuantity(new BigDecimal("50"));

        request.setItems(java.util.Arrays.asList(item1, item2));

        // Act & Assert
        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].quantity").value(100))
                .andExpect(jsonPath("$.items[1].quantity").value(50))
                .andReturn();

        // Verify both products have correct stock
        Thread.sleep(500);
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableStock").value(100));

        mockMvc.perform(get("/api/products/{id}", productId2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableStock").value(50));
    }

    // ==================== FLIPKART REFERENCE TEST ====================

    @Test
    @DisplayName("🛍️ Flipkart Sale: OUT movement with FLIPKART reference")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testFlipkartSale() throws Exception {
        // Setup: Add stock first
        addStockInSupplier(new BigDecimal("200"));

        // Arrange: Flipkart order
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.OUT);
        request.setReference(StockMovementReference.FLIPKART);
        request.setNotes("Daily Flipkart order fulfillment");

        StockMovementItemRequest item = new StockMovementItemRequest();
        item.setProductId(productId);
        item.setQuantity(new BigDecimal("75"));
        request.setItems(java.util.Arrays.asList(item));

        // Act & Assert
        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").value("FLIPKART"))
                .andReturn();

        // Verify stock = 125 (200 - 75)
        Thread.sleep(500);
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableStock").value(125));
    }

    // ==================== LIST AND FILTER TESTS ====================

    @Test
    @DisplayName("📋 List Movements: Get all stock movements with pagination")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testListMovements() throws Exception {
        // Setup: Create multiple movements
        addStockInSupplier(new BigDecimal("100"));
        removeStockOut(new BigDecimal("30"));
        removeStockOut(new BigDecimal("20"));

        // Act & Assert
        mockMvc.perform(get("/api/stock-movements?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.pageSize").value(10));
    }

    @Test
    @DisplayName("🔍 Filter by Type: Get only IN movements")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testFilterByTypeIN() throws Exception {
        // Setup
        addStockInSupplier(new BigDecimal("100"));
        removeStockOut(new BigDecimal("30"));

        // Act & Assert
        mockMvc.perform(get("/api/stock-movements?type=IN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].type").value("IN"));
    }

    @Test
    @DisplayName("🔍 Filter by Type: Get only OUT movements")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testFilterByTypeOUT() throws Exception {
        // Setup
        addStockInSupplier(new BigDecimal("100"));
        removeStockOut(new BigDecimal("30"));
        removeStockOut(new BigDecimal("20"));

        // Act & Assert
        mockMvc.perform(get("/api/stock-movements?type=OUT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.content[0].type").value("OUT"));
    }

    @Test
    @DisplayName("🔍 Filter by Reference: Get Meesho movements")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testFilterByReferenceMeesho() throws Exception {
        // Setup
        addStockInSupplier(new BigDecimal("100"));
        removeMeeshoOrder(new BigDecimal("20"));

        // Act & Assert
        mockMvc.perform(get("/api/stock-movements?reference=MEESHO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reference").value("MEESHO"));
    }

    @Test
    @DisplayName("📊 Get Single Movement: Retrieve specific movement by ID")
    @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
    public void testGetSingleMovement() throws Exception {
        // Setup: Create a movement
        MvcResult createResult = mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createStockInRequest()))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract ID from response
        String response = createResult.getResponse().getContentAsString();
        Long movementId = objectMapper.readTree(response).get("id").asLong();

        // Act & Assert
        mockMvc.perform(get("/api/stock-movements/{id}", movementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movementId))
                .andExpect(jsonPath("$.type").value("IN"));
    }

    // ==================== HELPER METHODS ====================

    private String createStockInRequest() throws Exception {
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.IN);
        request.setReference(StockMovementReference.SUPPLIER);
        request.setNotes("Helper stock in");

        StockMovementItemRequest item = new StockMovementItemRequest();
        item.setProductId(productId);
        item.setQuantity(new BigDecimal("100"));
        request.setItems(java.util.Arrays.asList(item));

        return objectMapper.writeValueAsString(request);
    }

    private void addStockInSupplier(BigDecimal quantity) throws Exception {
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.IN);
        request.setReference(StockMovementReference.SUPPLIER);

        StockMovementItemRequest item = new StockMovementItemRequest();
        item.setProductId(productId);
        item.setQuantity(quantity);
        request.setItems(java.util.Arrays.asList(item));

        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private void removeStockOut(BigDecimal quantity) throws Exception {
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.OUT);
        request.setReference(StockMovementReference.OFFLINE);

        StockMovementItemRequest item = new StockMovementItemRequest();
        item.setProductId(productId);
        item.setQuantity(quantity);
        request.setItems(java.util.Arrays.asList(item));

        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private void removeMeeshoOrder(BigDecimal quantity) throws Exception {
        CreateStockMovementRequest request = new CreateStockMovementRequest();
        request.setType(StockMovementType.OUT);
        request.setReference(StockMovementReference.MEESHO);

        StockMovementItemRequest item = new StockMovementItemRequest();
        item.setProductId(productId);
        item.setQuantity(quantity);
        request.setItems(java.util.Arrays.asList(item));

        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

}
