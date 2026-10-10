package com.grpproject.store.product.service;

import com.grpproject.store.product.model.NonPerishableProduct;
import com.grpproject.store.product.model.PerishableProduct;
import com.grpproject.store.product.model.Product;
import com.grpproject.store.product.repository.ProductFileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductService {

    private final ProductFileRepository repository;

    // Spring gives us the repository automatically (dependency injection)
    public ProductService(ProductFileRepository repository) {
        this.repository = repository;
    }

    // READ
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProduct(String id) {
        return repository.findById(id);
    }

    public List<Product> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return repository.findAll();
        }
        return repository.search(keyword.trim());
    }

    // CREATE
    public Product addProduct(String type, String name, String category, double price,
                              int stock, LocalDate expiryDate, Integer shelfLifeMonths) {
        validateCommon(name, category, price, stock);
        String id = generateId();
        Product product;

        if ("PERISHABLE".equals(type)) {
            validateExpiry(expiryDate);
            product = new PerishableProduct(id, name.trim(), category.trim(), price, stock, expiryDate);
        } else if ("NON_PERISHABLE".equals(type)) {
            validateShelfLife(shelfLifeMonths);
            product = new NonPerishableProduct(id, name.trim(), category.trim(), price, stock, shelfLifeMonths);
        } else {
            throw new IllegalArgumentException("Please choose a product type");
        }

        repository.save(product);
        return product;
    }

    // UPDATE
    public void updateProduct(String id, String name, String category, double price,
                              int stock, LocalDate expiryDate, Integer shelfLifeMonths) {
        Product existing = repository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        validateCommon(name, category, price, stock);

        existing.setName(name.trim());
        existing.setCategory(category.trim());
        existing.setPrice(price);
        existing.setStock(stock);

        if (existing instanceof PerishableProduct perishable) {
            validateExpiry(expiryDate);
            perishable.setExpiryDate(expiryDate);
        } else if (existing instanceof NonPerishableProduct nonPerishable) {
            validateShelfLife(shelfLifeMonths);
            nonPerishable.setShelfLifeMonths(shelfLifeMonths);
        }

        repository.update(existing);
    }

    // DELETE
    public void deleteProduct(String id) {
        if (!repository.delete(id)) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
    }

    // ---------- helpers ----------

    // Makes IDs like P001, P002, P005 (one higher than the biggest existing)
    private String generateId() {
        int max = 0;
        for (Product p : repository.findAll()) {
            try {
                int number = Integer.parseInt(p.getId().substring(1));
                if (number > max) max = number;
            } catch (NumberFormatException ignored) {
                // skip IDs that don't match the pattern
            }
        }
        return String.format("P%03d", max + 1);
    }

    private void validateCommon(String name, String category, double price, int stock) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (name.contains("|") || category.contains("|")) {
            throw new IllegalArgumentException("The '|' character is not allowed");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }

    private void validateExpiry(LocalDate expiryDate) {
        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date is required for perishable products");
        }
        if (expiryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiry date cannot be in the past");
        }
    }

    private void validateShelfLife(Integer months) {
        if (months == null || months <= 0) {
            throw new IllegalArgumentException("Shelf life (months) must be greater than 0");
        }
    }
}