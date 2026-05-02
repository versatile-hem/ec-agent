package com.ek.app.imports.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ImportSummaryResponse {

    private int totalRecords;
    private int success;
    private int failed;
    private List<ImportErrorDTO> errors = new ArrayList<>();

    public void addSuccess() {
        this.success++;
    }

    public void addFailure(int row, String error) {
        this.failed++;
        this.errors.add(new ImportErrorDTO(row, error));
    }
}
