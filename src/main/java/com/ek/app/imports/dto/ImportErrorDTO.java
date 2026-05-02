package com.ek.app.imports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImportErrorDTO {

    private int row;
    private String error;
}
