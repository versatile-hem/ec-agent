#!/bin/bash

# Generate comprehensive Stock Movement test report
BASE_URL="http://localhost:8080"

echo "=========================================="
echo "Stock Movement Module - Test Report"
echo "Date: $(date)"
echo "=========================================="
echo ""

# Get authentication token
TOKEN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@nexo.com","password":"admin123"}' | jq -r '.token')

AUTH_HEADER="Authorization: Bearer $TOKEN"

echo "📊 API ENDPOINTS TEST RESULTS"
echo "=========================================="
echo ""

echo "1️⃣  POST /api/stock-movements (Create IN Movement)"
echo "   Expected: 201 Created with full response"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/stock-movements" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "type": "IN",
    "reference": "SUPPLIER",
    "notes": "Test supplier delivery",
    "items": [{"productId": 1, "quantity": 50}]
  }')
HTTP_CODE=$(echo "$RESULT" | tail -1)
echo "   Status: $HTTP_CODE"
if [ "$HTTP_CODE" == "201" ]; then echo "   ✅ PASS"; else echo "   ❌ FAIL"; fi
echo ""

echo "2️⃣  POST /api/stock-movements (Create OUT Movement)"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/stock-movements" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "type": "OUT",
    "reference": "ORDER",
    "notes": "Test order fulfillment",
    "items": [{"productId": 1, "quantity": 20}]
  }')
HTTP_CODE=$(echo "$RESULT" | tail -1)
echo "   Status: $HTTP_CODE"
if [ "$HTTP_CODE" == "201" ]; then echo "   ✅ PASS"; else echo "   ❌ FAIL"; fi
echo ""

echo "3️⃣  GET /api/stock-movements (List all movements)"
RESULT=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/stock-movements?page=0&size=20" -H "$AUTH_HEADER")
HTTP_CODE=$(echo "$RESULT" | tail -1)
echo "   Status: $HTTP_CODE"
if [ "$HTTP_CODE" == "200" ]; then echo "   ✅ PASS"; else echo "   ❌ FAIL"; fi
echo ""

echo "4️⃣  GET /api/stock-movements?type=IN (Filter by type)"
RESULT=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/stock-movements?type=IN" -H "$AUTH_HEADER")
HTTP_CODE=$(echo "$RESULT" | tail -1)
echo "   Status: $HTTP_CODE"
if [ "$HTTP_CODE" == "200" ]; then echo "   ✅ PASS"; else echo "   ❌ FAIL"; fi
echo ""

echo "5️⃣  GET /api/products/1 (Verify availableStock)"
RESULT=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/products/1" -H "$AUTH_HEADER")
HTTP_CODE=$(echo "$RESULT" | tail -1)
echo "   Status: $HTTP_CODE"
if [ "$HTTP_CODE" == "200" ]; then 
  STOCK=$(echo "$RESULT" | head -1 | jq '.availableStock')
  echo "   Current available stock: $STOCK"
  echo "   ✅ PASS"
else 
  echo "   ❌ FAIL"
fi
echo ""

echo "❌ ERROR HANDLING TESTS"
echo "=========================================="
echo ""

echo "1️⃣  Test: Insufficient Stock (OUT > available)"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/stock-movements" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "type": "OUT",
    "reference": "ORDER",
    "notes": "This should fail",
    "items": [{"productId": 1, "quantity": 99999}]
  }')
HTTP_CODE=$(echo "$RESULT" | tail -1)
echo "   Status: $HTTP_CODE (Expected: 400)"
if [ "$HTTP_CODE" == "400" ]; then echo "   ✅ PASS"; else echo "   ❌ FAIL"; fi
echo ""

echo "2️⃣  Test: Invalid Product ID"
RESULT=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/stock-movements" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "type": "IN",
    "reference": "MANUAL",
    "notes": "Invalid product",
    "items": [{"productId": 99999, "quantity": 10}]
  }')
HTTP_CODE=$(echo "$RESULT" | tail -1)
echo "   Status: $HTTP_CODE (Expected: 404)"
if [ "$HTTP_CODE" == "404" ]; then echo "   ✅ PASS"; else echo "   ❌ FAIL"; fi
echo ""

echo "3️⃣  Test: Unauthorized (Missing token)"
RESULT=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/stock-movements")
HTTP_CODE=$(echo "$RESULT" | tail -1)
echo "   Status: $HTTP_CODE (Expected: 401)"
if [ "$HTTP_CODE" == "401" ]; then echo "   ✅ PASS"; else echo "   ❌ FAIL"; fi
echo ""

echo "📦 DATABASE VERIFICATION"
echo "=========================================="
psql -h localhost -U hem -d nexo << EOF
SELECT 
  COUNT(*) as "Total Stock Movements"
FROM stock_movement;

SELECT 
  type,
  COUNT(*) as "Count"
FROM stock_movement
GROUP BY type;

SELECT 
  p.product_id,
  p.name,
  p.sku,
  ip.on_hand_qty as "Current Stock"
FROM inventory_position ip
JOIN product p ON ip.product_id = p.product_id
WHERE p.product_id IN (1, 3, 4, 5)
ORDER BY p.product_id;
EOF

echo ""
echo "=========================================="
echo "✅ Test Report Generated"
echo "=========================================="
