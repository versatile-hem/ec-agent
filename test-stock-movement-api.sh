#!/bin/bash

# Stock Movement API Test Script
# This script tests the Stock Movement functionality with curl commands

BASE_URL="http://localhost:8080"
CONTENT_TYPE="Content-Type: application/json"

echo "=========================================="
echo "Stock Movement API Testing"
echo "=========================================="
echo ""

# Test 1: Create IN stock movement (Receive stock from supplier)
echo "TEST 1: Create Stock Movement - IN (Supplier Delivery)"
echo "-------------------------------------------------------------"
curl -X POST "$BASE_URL/api/stock-movements" \
  -H "$CONTENT_TYPE" \
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
  }' | jq '.'
echo ""
echo ""

# Test 2: Create OUT stock movement (Order fulfillment)
echo "TEST 2: Create Stock Movement - OUT (Order Fulfillment)"
echo "-------------------------------------------------------------"
curl -X POST "$BASE_URL/api/stock-movements" \
  -H "$CONTENT_TYPE" \
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
  }' | jq '.'
echo ""
echo ""

# Test 3: Create RETURN stock movement
echo "TEST 3: Create Stock Movement - IN (Customer Return)"
echo "-------------------------------------------------------------"
curl -X POST "$BASE_URL/api/stock-movements" \
  -H "$CONTENT_TYPE" \
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
echo "TEST 4: Get All Stock Movements"
echo "-------------------------------------------------------------"
curl -s "$BASE_URL/api/stock-movements?page=0&size=10" | jq '.'
echo ""
echo ""

# Test 5: Get stock movements filtered by type (IN only)
echo "TEST 5: Get Stock Movements - Filtered by Type (IN)"
echo "-------------------------------------------------------------"
curl -s "$BASE_URL/api/stock-movements?type=IN&page=0&size=10" | jq '.'
echo ""
echo ""

# Test 6: Get stock movements filtered by type (OUT only)
echo "TEST 6: Get Stock Movements - Filtered by Type (OUT)"
echo "-------------------------------------------------------------"
curl -s "$BASE_URL/api/stock-movements?type=OUT&page=0&size=10" | jq '.'
echo ""
echo ""

# Test 7: Get first stock movement details
echo "TEST 7: Get Single Stock Movement Details"
echo "-------------------------------------------------------------"
echo "Note: Replace {id} with actual movement ID from results above"
echo ""

# Test 8: Get updated product with available stock
echo "TEST 8: Get Product with Updated Available Stock"
echo "-------------------------------------------------------------"
curl -s "$BASE_URL/api/products/1" | jq '.'
echo ""
echo ""

# Test 9: Test error case - Insufficient stock
echo "TEST 9: Test Error Case - OUT Movement with Insufficient Stock"
echo "-------------------------------------------------------------"
curl -X POST "$BASE_URL/api/stock-movements" \
  -H "$CONTENT_TYPE" \
  -d '{
    "type": "OUT",
    "reference": "ORDER",
    "notes": "This should fail - too much stock requested",
    "items": [
      {
        "productId": 1,
        "quantity": 9999
      }
    ]
  }' | jq '.'
echo ""
echo ""

# Test 10: Test error case - Invalid product
echo "TEST 10: Test Error Case - Invalid Product ID"
echo "-------------------------------------------------------------"
curl -X POST "$BASE_URL/api/stock-movements" \
  -H "$CONTENT_TYPE" \
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

echo "=========================================="
echo "Testing Complete!"
echo "=========================================="
