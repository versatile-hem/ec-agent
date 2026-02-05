package com.ek.app.reports;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportDefinition {

    private String code; // STOCK_REPORT
    private String name; // Stock Summary
    private String description;

    public ReportDefinition(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
