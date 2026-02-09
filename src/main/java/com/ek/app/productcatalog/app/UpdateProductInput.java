package com.ek.app.productcatalog.dtos;

import lombok.Data;

@Data
public class UpdateProductInput {
	

	 private Long productId;
	    private String name;
	    private String description;
	    private String baseUnit;


}
