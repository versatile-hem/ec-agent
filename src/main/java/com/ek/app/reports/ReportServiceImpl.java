package com.ek.app.reports;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public List<ReportDefinition> getAllReports() {

        return List.of(
                new ReportDefinition("INVENTORY_MOVEMENT", "Inventory Movements"),
                new ReportDefinition("STOCK_SUMMARY", "Stock Summary"),
                new ReportDefinition("SALES", "Sales Report"),
                new ReportDefinition("DAMAGE", "Damage Report"),
                new ReportDefinition("REPAIR", "Repair Report"));
    }

}
