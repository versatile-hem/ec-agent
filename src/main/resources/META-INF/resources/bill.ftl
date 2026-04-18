<#ftl output_format="XHTML" auto_esc=true>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <style>
        :root {
            --bg: #f4f7fb;
            --panel: #ffffff;
            --ink: #0f172a;
            --muted: #475569;
            --line: #dbe2ea;
            --line-strong: #aab6c5;
            --brand: #0f4c81;
            --brand-soft: #e8f1f9;
            --ok: #166534;
            --warn: #b45309;
            --radius: 10px;
        }

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            background: var(--bg);
            color: var(--ink);
            font-family: "Segoe UI", Tahoma, "Helvetica Neue", Arial, sans-serif;
            font-size: 12px;
            line-height: 1.45;
            -webkit-print-color-adjust: exact;
            print-color-adjust: exact;
        }

        .invoice {
            max-width: 980px;
            margin: 20px auto;
            background: var(--panel);
            border: 1px solid var(--line);
            border-radius: var(--radius);
            overflow: hidden;
        }

        .topbar {
            background: linear-gradient(135deg, #0f4c81, #1f6aa5);
            color: #fff;
            padding: 20px 24px;
        }

        .topbar-main {
            display: table;
            width: 100%;
        }

        .topbar-left,
        .topbar-right {
            display: table-cell;
            vertical-align: top;
            width: 50%;
        }

        .brand {
            font-size: 22px;
            font-weight: 700;
            letter-spacing: 0.08em;
            text-transform: uppercase;
        }

        .company-block {
            margin-top: 10px;
            font-size: 11px;
            line-height: 1.45;
            opacity: 0.98;
            text-align: left;
            color: #0b3d67;
            background: #eef5fc;
            border: 1px solid #cfe0f2;
            border-radius: 8px;
            padding: 10px 12px;
        }

        .company-name {
            font-size: 14px;
            font-weight: 700;
            letter-spacing: 0.02em;
            text-transform: uppercase;
            margin-bottom: 6px;
        }

        .company-line {
            margin: 2px 0;
        }

        .meta {
            margin-top: 6px;
            font-size: 11px;
            opacity: 0.95;
        }

        .invoice-title {
            text-align: right;
            font-size: 28px;
            font-weight: 700;
            letter-spacing: 0.04em;
            margin: 0;
        }

        .invoice-number {
            text-align: right;
            margin-top: 6px;
            font-size: 12px;
        }

        .invoice-tax {
            text-align: right;
            font-size: 11px;
            line-height: 1.4;
            opacity: 0.98;
            color: #0b3d67;
            background: #eef5fc;
            border: 1px solid #cfe0f2;
            border-radius: 8px;
            padding: 6px 8px;
            display: inline-block;
        }

        .company-row {
            display: table;
            width: 100%;
        }

        .company-left,
        .company-right {
            display: table-cell;
            vertical-align: top;
        }

        .company-left {
            width: 72%;
            padding-right: 10px;
        }

        .company-right {
            width: 28%;
            text-align: right;
        }

        .content {
            padding: 22px 24px 18px;
        }

        .header-divider {
            border-top: 2px solid #d6e4f2;
            margin: 0 24px;
        }

        .grid {
            display: table;
            width: 100%;
            border-spacing: 12px 0;
            margin: 0 -12px;
        }

        .panel {
            display: table-cell;
            width: 50%;
            background: #fdfefe;
            border: 1px solid var(--line);
            border-radius: 8px;
            padding: 12px;
            vertical-align: top;
        }

        .panel h4 {
            margin: 0 0 8px;
            font-size: 12px;
            color: var(--brand);
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }

        .kv {
            margin: 0;
            padding: 0;
            list-style: none;
        }

        .kv li {
            margin: 3px 0;
            color: var(--muted);
        }

        .kv b {
            color: var(--ink);
            font-weight: 600;
        }

        .items-wrap {
            margin-top: 14px;
            border: 1px solid var(--line);
            border-radius: 8px;
            overflow: hidden;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        .items thead th {
            background: var(--brand-soft);
            color: #0b3d67;
            font-size: 11px;
            font-weight: 700;
            padding: 10px 8px;
            border-bottom: 1px solid var(--line-strong);
            text-align: left;
            text-transform: uppercase;
            letter-spacing: 0.03em;
        }

        .items tbody td {
            padding: 8px;
            border-bottom: 1px solid var(--line);
            color: #1e293b;
            vertical-align: top;
        }

        .items tbody tr:nth-child(even) td {
            background: #fbfdff;
        }

        .items tbody tr:last-child td {
            border-bottom: 0;
        }

        .right {
            text-align: right;
            white-space: nowrap;
        }

        .summary {
            margin-top: 14px;
            display: table;
            width: 100%;
        }

        .summary-spacer,
        .summary-box {
            display: table-cell;
            vertical-align: top;
        }

        .summary-spacer {
            width: 58%;
        }

        .summary-box {
            width: 42%;
            border: 1px solid var(--line);
            border-radius: 8px;
            overflow: hidden;
        }

        .totals td {
            padding: 8px 10px;
            border-bottom: 1px solid var(--line);
        }

        .totals tr:last-child td {
            border-bottom: 0;
        }

        .totals tr.total-row td {
            background: #f1f7fd;
            border-top: 1px solid var(--line-strong);
            font-weight: 700;
            color: #0b3d67;
            font-size: 13px;
        }

        .foot {
            margin-top: 12px;
            border-top: 1px dashed var(--line-strong);
            padding-top: 10px;
            color: var(--muted);
            font-size: 11px;
            display: table;
            width: 100%;
        }

        .foot-left,
        .foot-right {
            display: table-cell;
            width: 50%;
            vertical-align: top;
        }

        .foot-right {
            text-align: right;
        }

        .signatory {
            margin-top: 28px;
            padding-top: 8px;
            border-top: 1px solid var(--line-strong);
            display: inline-block;
            min-width: 190px;
            text-align: center;
            color: var(--ink);
            font-weight: 600;
        }

        .status {
            display: inline-block;
            padding: 2px 8px;
            border: 1px solid #9ac9a8;
            border-radius: 999px;
            color: var(--ok);
            background: #edf9f1;
            font-weight: 700;
            font-size: 10px;
            letter-spacing: 0.04em;
            text-transform: uppercase;
        }

        .status.pending,
        .status.unpaid {
            border-color: #f2cf95;
            color: var(--warn);
            background: #fff7ea;
        }

        @media print {
            body {
                background: #fff;
            }

            .invoice {
                margin: 0;
                border: 0;
                border-radius: 0;
            }
        }
    </style>
</head>

<body>
<div class="invoice">
    <div class="topbar">
        <div class="topbar-main">
            <div class="topbar-left">
                <div class="brand">Enterprise Billing</div>
                <div class="meta">Tax-compliant invoice statement</div>
            </div>
            <div class="topbar-right">
                <h1 class="invoice-title">Tax Invoice</h1>
                <div class="invoice-number">Invoice No: <b>${bill.billNo}</b></div>
            </div>
        </div>
        <div class="company-block">
            <div class="company-row">
                <div class="company-left">
                    <div class="company-name">EARENDEL ONLINE SERVICES PRIVATE LIMITED</div>
                    <div class="company-line">Address: 588A, Sector 46, Faridabad, Haryana, India</div>
                    <div class="company-line">Phone: +91-7988033662 | Email: contact@earendel.com</div>
                    <div class="company-line">Web: www.earendel.com</div>
                </div>
                <div class="company-right">
                    <div class="invoice-tax">
                        <div>GSTIN: 06AAICE7379R1ZN</div>
                        <div>PAN: AAICE7379R</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="header-divider"></div>

    <div class="content">
        <div class="grid">
            <div class="panel">
                <h4>Bill Details</h4>
                <ul class="kv">
                    <li><b>Bill No:</b> ${bill.billNo}</li>
                    <li><b>Date:</b> ${bill.billDate}</li>
                    <li><b>Payment Mode:</b> ${bill.paymentMode}</li>
                </ul>
            </div>
            <div class="panel">
                <h4>Customer</h4>
                <ul class="kv">
                    <li><b>Name:</b> ${bill.customerName}</li>
                    <li><b>Phone:</b> ${bill.customerPhone}</li>
                    <li>
                        <b>Status:</b>
                        <#assign st = (bill.status!"")?lower_case>
                        <span class="status <#if st == "pending" || st == "unpaid">pending</#if>">${bill.status!"N/A"}</span>
                    </li>
                </ul>
            </div>
        </div>

        <div class="items-wrap">
            <table class="items">
                <thead>
                    <tr>
                        <th style="width:5%">#</th>
                        <th style="width:27%">Product</th>
                        <th style="width:11%">SKU</th>
                        <th style="width:7%" class="right">Qty</th>
                        <th style="width:12%" class="right">Unit Price</th>
                        <th style="width:12%" class="right">Taxable</th>
                        <th style="width:8%" class="right">GST %</th>
                        <th style="width:8%" class="right">Tax</th>
                        <th style="width:10%" class="right">Line Total</th>
                    </tr>
                </thead>
                <tbody>
                    <#list bill.items as item>
                    <tr>
                        <td>${item_index + 1}</td>
                        <td>${item.productName!item.product_title}</td>
                        <td>${item.sku!"-"}</td>
                        <td class="right">${item.quantity}</td>
                        <td class="right">${item.unitPrice}</td>
                        <td class="right">${item.taxableValue!item.lineTotal}</td>
                        <td class="right">${item.gst!0}</td>
                        <td class="right">${item.tax!0}</td>
                        <td class="right">${item.finalAmount!item.lineTotal}</td>
                    </tr>
                    </#list>
                </tbody>
            </table>
        </div>

        <div class="summary">
            <div class="summary-spacer"></div>
            <div class="summary-box">
                <table class="totals">
                    <tr>
                        <td>Subtotal</td>
                        <td class="right">${bill.subtotal}</td>
                    </tr>
                    <tr>
                        <td>Tax</td>
                        <td class="right">${bill.taxAmount}</td>
                    </tr>
                    <tr>
                        <td>Discount</td>
                        <td class="right">${bill.discountAmount}</td>
                    </tr>
                    <tr class="total-row">
                        <td>Total Payable</td>
                        <td class="right">${bill.totalAmount}</td>
                    </tr>
                </table>
            </div>
        </div>

        <div class="foot">
            <div class="foot-left">
                This is a system-generated tax invoice.
            </div>
            <div class="foot-right">
                <div>Generated on ${bill.billDate}</div>
                <div class="signatory">Authorised Signatory</div>
            </div>
        </div>
    </div>
</div>

</body>
</html>