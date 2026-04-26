-- Test Data Script for Stock Movement Module
-- Run this script to populate initial test data

-- 1. Check and insert inventory positions for products with varied stock levels
INSERT INTO inventory_position (on_hand_qty, reserved_qty, product_id, avg_cost, last_modifed_on)
SELECT 
  CASE 
    WHEN p.product_id = 1 THEN 1000.00
    WHEN p.product_id = 3 THEN 500.00
    WHEN p.product_id = 4 THEN 250.00
    WHEN p.product_id = 5 THEN 750.00
    WHEN p.product_id = 6 THEN 300.00
    ELSE 200.00
  END as on_hand_qty,
  0 as reserved_qty, 
  p.product_id,
  100.00 as avg_cost,
  NOW() as last_modifed_on
FROM product p
WHERE NOT EXISTS (SELECT 1 FROM inventory_position WHERE product_id = p.product_id)
LIMIT 10;

-- 2. Display current inventory
SELECT 
  ip.id,
  p.product_id,
  p.name,
  p.sku,
  ip.on_hand_qty as "Available Stock",
  ip.reserved_qty as "Reserved",
  (ip.on_hand_qty - ip.reserved_qty) as "Free Stock"
FROM inventory_position ip
JOIN product p ON ip.product_id = p.product_id
ORDER BY p.product_id
LIMIT 15;
