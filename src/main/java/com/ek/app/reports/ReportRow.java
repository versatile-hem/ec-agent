package com.ek.app.reports;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ReportRow {

    private String product;
    private BigDecimal quantity;
    private String channel;
    private LocalDate date;
}
