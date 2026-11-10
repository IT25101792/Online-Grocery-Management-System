package com.grpproject.store.product.repository;

import com.grpproject.store.product.model.Product;

import java.util.List;

public interface ProductRepository {
    void save(Product p);
    Product findById(int id);
    List<Product> findAll();
    void update(Product p);
    void delete(int id);
}