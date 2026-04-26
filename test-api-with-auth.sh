#!/bin/bash

# Stock Movement API Test Script with Authentication
# This script tests the Stock Movement functionality with proper JWT authentication

BASE_URL="http://localhost:8080"
CONTENT_TYPE="Content-Type: application/json"

echo "=========================================="
echo "Stock Movement API Testing with Auth"
echo "=========================================="
echo ""

# Step 1: Authenticate and get JWT token
echo "STEP 1: Authenticate User"
echo "-------------------------------------------------------------"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "$CONTENT_TYPE" \
  -d '{
    "username": "admin@nexo.com",
    "password": "admin123"
  }')

echo "$LOGIN_RESPONSE" | jq '.'
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token')
echo ""
echo "Token obtained: ${TOKEN:0:50}..."
echo ""
echo ""

# Set authorization header
AUTH_HEADER="Authorization: Bearer $TOKEN"

# Test 1: Create IN stock movement (Receive stock from supplier)
echo "TEST 1: Create Stock Movement - IN (Supplier Delivery)"
echo "-------------------------------------------------------------"
RESPONSE1=$(curl -s -X POST "$BASE_URL/api/stock-movements" \
  -H "$CONTENT_TYPE" \
  -H "$AUTH_HEADER" \
  -d '{
    "type": "IN",
    "reference": "SUPPLIER",
    "notes": "Stock received from supplier - ORD-2026-001",
    "items": [
      {
        "productId": 1,
        "quantity": 100,
        "reference": "ChallanNo#12345"
      },
      {
        "productId": 3,
        "quantity": 50,
        "reference": "ChallanNo#12345"
      }
    ]
  }')

echo "$RESPONSE1" | jq '.'
MOVEMENT_ID_1=$(echo "$RESPONSE1" | jq -r '.id // empty')
echo ""
echo ""

# Test 2: Create OUT stock movement (Order fulfillment)
echo "TEST 2: Create Stock Movement - OUT (Order Fulfillment)"
echo "-------------------------------------------------------------"
RESPONSE2=$(curl -s -X POST "$BASE_URL/api/stock-movements" \
  -H "$CONTENT_TYPE" \
  -H "$AUTH_HEADER" \
  -d '{
    "type": "OUT",
    "reference": "ORDER",
    "notes": "Fulfilling customer order #ORD-2026-999",
    "items": [
      {
        "productId": 1,
        "quantity": 25
      },
      {
        "productId": 3,
        "quantity": 15
      }
    ]
  }')

echo "$RESPONSE2" | jq '.'
MOVEMENT_ID_2=$(echo "$RESPONSE2" | jq -r '.id // empty')
echo ""
echo ""

# Test 3: Create RETURN stock movement
echo "TEST 3: Create Stock Movement - IN (Customer Return)"
echo "-------------------------------------------------------------"
curl -s -X POST "$BASE_URL/api/stock-movements" \
  -H "$CONTENT_TYPE" \
  -H "$AUTH_HEADER" \
  -d '{
    "type": "IN",
    "reference": "RETURN",
    "notes": "Customer return - Order #ORD-2026-888",
    "items": [
      {
        "productId": 1,
        "quantity": 5
      },
      {
        "productId": 4,
        "quantity": 3
      }
    ]
  }' | jq '.'
echo ""
echo ""

# Test 4: Get all stock movements (paginated)
echo "TEST 4: Get All Stock Movements (Paginated)"
echo "-------------------------------------------------------------"
curl -s "$BASE_URL/api/stock-movements?page=0&size=10" \
  -H "$AUTH_HEADER" | jq '.'
echo ""
echo ""

# Test 5: Get stock movements filtered by type (IN only)
echo "TEST 5: Get Stock Movements - Filtered by Type (IN)"
echo "-------------------------------------------------------------"
curl -s "$BASE_URL/api/stock-movements?type=IN&page=0&size=10" \
  -H "$AUTH_HEADER" | jq '.'
echo ""
echo ""

# Test 6: Get stock movements filtered by type (OUT only)
echo "TEST 6: Get Stock Movements - Filtered by Type (OUT)"
echo "-------------------------------------------------------------"
curl -s "$BASE_URL/api/stock-movements?type=OUT&page=0&size=10" \
  -H "$AUTH_HEADER" | jq '.'
echo ""
echo ""

# Test 7: Get first stock movement details
if [ -n "$MOVEMENT_ID_1" ]; then
  echo "TEST 7: Get Single Stock Movement Details (ID: $MOVEMENT_ID_1)"
  echo "-------------------------------------------------------------"
  curl -s "$BASE_URL/api/stock-movements/$MOVEMENT_ID_1" \
    -H "$AUTH_HEADER" | jq '.'
  echo ""
  echo ""
fi

# Test 8: Get product with available stock (Product ID: 1)
echo "TEST 8: Get Product with Updated Available Stock (Product ID: 1)"
echo "-------------------------------------------------------------"
curl -s "$BASE_URL/api/products/1" \
  -H "$AUTH_HEADER" | jq '.'
echo ""
echo ""

# Test 9: Test error case - Insufficient stock
echo "TEST 9: Test Error Case - OUT Movement with Insufficient Stock"
echo "-------------------------------------------------------------"
curl -s -X POST "$BASE_URL/api/stock-movements" \
  -H "$CONTENT_TYPE" \
  -H "$AUTH_HEADER" \
  -d '{
    "type": "OUT",
    "reference": "ORDER",
    "notes": "This should fail - too much stock requested",
    "items": [
      {
        "productId": 1,
        "quantity": 99999
      }
    ]
  }' | jq '.'
echo ""
echo ""

# Test 10: Test error case - Invalid product
echo "TEST 10: Test Error Case - Invalid Product ID"
echo "-------------------------------------------------------------"
curl -s -X POST "$BASE_URL/api/stock-movements" \
  -H "$CONTENT_TYPE" \
  -H "$AUTH_HEADER" \
  -d '{
    "type": "IN",
    "reference": "MANUAL",
    "notes": "This should fail - invalid product",
    "items": [
      {
        "productId": 99999,
        "quantity": 10
      }
    ]
  }' | jq '.'
echo ""
echo ""

# Test 11: Check inventory in database
echo "TEST 11: Verify Inventory Updates in Database"
echo "-------------------------------------------------------------"
psql -h localhost -U hem -d nexo << EOF
SELECT 
  p.product_id,
  p.name,
  p.sku,
  ip.on_hand_qty,
  ip.reserved_qty,
  (ip.on_hand_qty - ip.reserved_qty) as free_stock
FROM inventory_position ip
JOIN product p ON ip.product_id = p.product_id
WHERE p.product_id IN (1, 3, 4)
ORDER BY p.product_id;
EOF

echo ""
echo "=========================================="
echo "Testing Complete!"
echo "=========================================="
