package com.ek.app.reports;

import com.vaadin.flow.data.provider.DataProvider;

public interface ReportService {

    DataProvider<ReportDefinition, Void> getAllReports();

}
