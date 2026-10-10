package com.grpproject.store.product.service;

import com.grpproject.store.product.model.NonPerishableProduct;
import com.grpproject.store.product.model.PerishableProduct;
import com.grpproject.store.product.model.Product;
import com.grpproject.store.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    // Spring injects the repository for us (dependency injection)
    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    // CREATE
    public void addProduct(Product p) {
        validate(p, true);
        p.setProductId(generateNextId());
        repo.save(p);
    }

    // READ
    public Product getProduct(int id) {
        return repo.findById(id);
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return repo.findAll();
        }
        String key = keyword.trim().toLowerCase();
        List<Product> results = new ArrayList<>();
        for (Product p : repo.findAll()) {
            if (p.getName().toLowerCase().contains(key)
                    || p.getCategory().toLowerCase().contains(key)) {
                results.add(p);
            }
        }
        return results;
    }

    // UPDATE
    public void updateProduct(Product p) {
        if (repo.findById(p.getProductId()) == null) {
            throw new IllegalArgumentException("Product not found: " + p.getProductId());
        }
        validate(p, false);
        repo.update(p);
    }

    // DELETE
    public void deleteProduct(int id) {
        if (repo.findById(id) == null) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        repo.delete(id);
    }

    // Used by Cart / Order components when an order is placed
    public void reduceStock(int id, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        Product p = repo.findById(id);
        if (p == null) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        if (p.getStockQty() < qty) {
            throw new IllegalArgumentException("Not enough stock for " + p.getName()
                    + " (available: " + p.getStockQty() + ")");
        }
        p.setStockQty(p.getStockQty() - qty);
        repo.update(p);
    }

    // Used when new stock arrives or an order is cancelled
    public void restock(int id, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        Product p = repo.findById(id);
        if (p == null) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        p.setStockQty(p.getStockQty() + qty);
        repo.update(p);
    }

    public List<Product> getLowStockProducts() {
        List<Product> low = new ArrayList<>();
        for (Product p : repo.findAll()) {
            if (p.isLowStock()) {
                low.add(p);
            }
        }
        return low;
    }

    // ---------- helpers ----------

    // One higher than the biggest existing productId
    private int generateNextId() {
        int max = 0;
        for (Product p : repo.findAll()) {
            if (p.getProductId() > max) {
                max = p.getProductId();
            }
        }
        return max + 1;
    }

    private void validate(Product p, boolean isNew) {
        if (p.getName() == null || p.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (p.getCategory() == null || p.getCategory().isBlank()) {
            throw new IllegalArgumentException("Category is required");
        }
        p.setName(p.getName().trim());
        p.setCategory(p.getCategory().trim());

        if (p.getName().contains("|") || p.getCategory().contains("|")) {
            throw new IllegalArgumentException("The '|' character is not allowed");
        }
        if (p.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (p.getStockQty() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        if (p.getReorderLevel() < 0) {
            throw new IllegalArgumentException("Reorder level cannot be negative");
        }

        if (p instanceof PerishableProduct perishable) {
            if (perishable.getExpiryDate() == null) {
                throw new IllegalArgumentException("Expiry date is required for perishable products");
            }
            if (isNew && perishable.getExpiryDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Expiry date cannot be in the past");
            }
        } else if (p instanceof NonPerishableProduct nonPerishable) {
            String packaging = nonPerishable.getPackagingType();
            if (packaging == null || packaging.isBlank()) {
                throw new IllegalArgumentException("Packaging type is required");
            }
            if (packaging.contains("|")) {
                throw new IllegalArgumentException("The '|' character is not allowed");
            }
            nonPerishable.setPackagingType(packaging.trim());
        }
    }
}