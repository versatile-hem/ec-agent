package com.ek.app.nexo.service;

import java.util.List;

import com.ek.app.nexo.dto.ProductDTO;

public interface ProductService {

    ProductDTO create(ProductDTO request);

    List<ProductDTO> getAll();

    ProductDTO getById(Long id);

    ProductDTO getByBarcode(String barcode);

    ProductDTO update(Long id, ProductDTO request);
}
