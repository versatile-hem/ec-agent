package com.ek.app.gui;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard • StockFlow")
@CssImport(value = "./themes/stockflow/components.css")
public class DashboardView extends Div {

    public DashboardView() {
        addClassName("dashboard");

        // Responsive card area using CSS grid
        Div grid = new Div();
        grid.addClassName("cards-grid");

        // Row 1: KPIs
        grid.add(new KpiCard("Total Products", "1,284", "+2.3%"));
        grid.add(new KpiCard("Inventory Value", "$42,500", "+4.2%"));
        grid.add(new KpiCard("Low Stock Alerts", "24", "-1.0%"));
        grid.add(new KpiCard("Out of Stock", "8", "+0.5%"));

        // Row 2: charts + alerts
        //grid.add(new AreaChartCard("Stock Level Trend", "Daily inventory movement", "Last 7 days"));
       // grid.add(new DonutChartCard("Stock by Category", "Distribution of inventory assets"));
       // grid.add(new AlertsCard("Stock Alerts"));

        // Row 3: inventory grid + movement + history
        //grid.add(new ProductInventoryGrid());
        //grid.add(new MovementCard("Stock Movement"));
       // grid.add(new AlertsCard("Recent History")); // reuse simple list card as placeholder

        add(grid);
    }


}
