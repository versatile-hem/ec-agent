package com.ek.app.reports;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.DataProvider;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public DataProvider<ReportDefinition, Void> getAllReports() {

        List<ReportDefinition> reports = List.of(
                new ReportDefinition("INVENTORY_MOVEMENT", "Inventory Movements"),
                new ReportDefinition("STOCK_SUMMARY", "Stock Summary"),
                new ReportDefinition("SALES", "Sales Report"),
                new ReportDefinition("DAMAGE", "Damage Report"),
                new ReportDefinition("REPAIR", "Repair Report"));

        return DataProvider.fromCallbacks(
                query -> reports.stream(),
                query -> reports.size());

    }

}
