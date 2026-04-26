# Flipkart Label Agent (Spring Boot) - Starter

This starter project is a Spring Boot application that:
- Fetches shipments with pending labels from Flipkart (placeholder endpoints)
- Generates label PDFs via Flipkart API
- Prints labels to a local TSC/USB printer using Java PrintService
- Marks shipments as RTD on Flipkart after successful print
- Sends email alerts (Gmail) on failures (printing, label gen, etc.)

## How to Run

### Local Development
1. Ensure PostgreSQL is running on localhost:5432 with database `nexo`
2. Configure local settings in `src/main/resources/application.properties`:
   - Database: `spring.datasource.url` should point to localhost
   - Printer: Set `printer.name` to match your TSC printer name
   - Email: Update `spring.mail.username` and `spring.mail.password` (use Gmail app password)
   - Alerts: Set `alert.recipients` for email notifications
3. Set environment variable for Flipkart token:
   ```bash
   export FLIPKART_TOKEN=your_token_here
   ```
4. Run locally with default properties (development mode):
   ```bash
   mvn spring-boot:run
   # OR
   mvn clean install
   java -jar target/flipkart-label-agent-0.0.1-SNAPSHOT.jar
   ```

### Production Deployment
1. Configure production settings in `src/main/resources/application-prod.properties`
2. Activate production profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
   # OR
   java -jar target/flipkart-label-agent-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
   ```

## Notes
- Flipkart API endpoints in `FlipkartClient` are placeholders; replace with actual endpoints from Flipkart Seller API docs.
- For TSC printers, if PDFs do not print correctly, consider converting PDF to image or sending TSPL commands.
- For USB printers on servers, ensure the JVM has access to the local print spooler and the printer drivers are installed.

## MCP API (OpenAI)

Set OpenAI API key before running:

```bash
export OPENAI_API_KEY=your_openai_api_key
```

Chat endpoint:

```bash
curl --noproxy '*' -sS -X POST 'http://127.0.0.1:80/api/mcp/chat' \
   -H 'Content-Type: application/json' \
   -d '{"prompt":"Say hello in one line"}'
```

Structured data endpoint:

```bash
curl --noproxy '*' -sS -X POST 'http://127.0.0.1:80/api/mcp/structured-data' \
   -H 'Content-Type: application/json' \
   -d '{"prompt":"Return JSON with fields title, priority, etaDays"}'
```

Expected behavior:
- `/api/mcp/chat` returns JSON with `prompt` and `response`.
- `/api/mcp/structured-data` returns model-generated JSON object.

If you receive redirect to `/login`, update security to permit `/api/mcp/**` for unauthenticated access.
# ec-agent
<<<<<<< HEAD



🏢 Ecommerce Companion – Feature Segregation (Domain Wise)
1. 📊 Dashboard
  - Total Stock Value
-  Total Sales
- Marketplace Wise Revenue
- Top Selling SKU
- Dead Stock Report
- Alerts (Low Stock, High Returns, Blocked Listings)
2. 📦 Inventory Management
2.1 Product Master
- Create / Edit Product
- SKU Management
- Barcode Mapping (Internal + Marketplace)
- Category & Brand
- GST Slab Mapping
- HSN Code
2.2 Stock Control
- Current Stock (Inventory Position Snapshot)
- Low Stock Alert
- Reorder Level Setup
- Reserved Stock
- Damaged / Expired Stock
2.3 Stock Movement
Stock In (GRN)
Stock Out
Transfer Between Locations
Adjustment Entry
Inventory Valuation Ledger
Batch / Lot Tracking
3. 🛒 Purchase Management
- 3.1 Purchase Order
- Create PO
- PO Approval
- Pending PO
- Closed PO
- Supplier Comparison
3.2 Goods Receipt (GRN)
Create GRN from PO
Barcode Scan Entry
Lot Creation
Quality Check
3.3 Purchase Invoice
Vendor Bill Entry
GST Input Credit
Expense Allocation
Invoice vs PO Matching
3.4 Supplier Management
Supplier Master
Payment Terms
Supplier Ledger
4. 🧾 Sales & Bill Book
- 4.1 Bill Book
- Create Sales Invoice
- Manual Bill Entry
- GST Invoice
- Credit Note
- Debit Note
- 4.2 Payments
- Payment Received
- Refund Management
- Settlement Reconciliation
- 4.3 Customer Management
- Customer Master
- Customer Ledger
- Outstanding Report
5. 🛍 Marketplace Integration
- 5.1 Flipkart
- Listings
- Active Listings
- Blocked Listings
- Catalog Sync
- Orders
- New Orders
- Cancelled Orders
- Returns
- RTO
- Insights
- Sales Analytics
- Return Rate
- Commission Breakdown
- Settlement Report
- Inventory Mismatch
- 5.2 Meesho
- Listings
- Live Products
- Price Control
- Orders
- New Orders
- Delivered Orders
- Cancelled Orders
- Return Orders
- Insights
- Order Summary
- Profit Report
- Returns Analysis
- Settlement Breakdown
6. 💰 Accounting & Finance
- 6.1 Ledger
- Inventory Valuation
- Purchase Ledger
- Sales Ledger
- Expense Ledger
- Marketplace Commission Ledger
- 6.2 Reports
- Profit & Loss (P&L)
- Balance Sheet
- GST Report
- Input vs Output GST
- Monthly Tax Summary
7. 📈 Reporting & Analytics
- 7.1 Business Reports
- Marketplace Wise Profit
- SKU Wise Profit
- Category Wise Profit
- 7.2 Inventory Reports
- Stock Ageing
- Fast vs Slow Moving
- Reorder Suggestions
8. ⚙ Automation & System
- 8.1 Automation Rules
- Auto PO Generation
- Auto Reorder
- Price Sync Rule
- Auto Stock Sync with Marketplace
- 8.2 Alerts
- Low Stock
- Negative Margin
- High Return Rate
- Blocked Listings
- 8.3 API & Integrations
- Flipkart API
- Meesho API
- WhatsApp Bot Integration
- Barcode Device Configuration
9. 👤 Admin & Settings
- User Roles & Permissions
- Warehouse Setup
- GST Configuration
- Company Profile
- Financial Year Setup
- Backup & Restore
- Audit Log
=======
>>>>>>> 23abb2f (first commit from omibook)
