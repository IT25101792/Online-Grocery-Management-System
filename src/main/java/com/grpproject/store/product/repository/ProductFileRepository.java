package com.grpproject.store.product.repository;

import com.grpproject.store.product.model.NonPerishableProduct;
import com.grpproject.store.product.model.PerishableProduct;
import com.grpproject.store.product.model.Product;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductFileRepository {

    private static final String FILE_PATH = "data/products.txt";

    // Creates the data folder and file if they don't exist yet
    public ProductFileRepository() {
        try {
            File file = new File(FILE_PATH);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile(); // does nothing if it already exists
        } catch (IOException e) {
            throw new RuntimeException("Could not create products file", e);
        }
    }

    // CREATE: add one line at the end of the file
    public void save(Product product) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(product.toFileString());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Could not save product", e);
        }
    }

    // READ: all products
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                Product p = parseLine(line);
                if (p != null) products.add(p);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read products", e);
        }
        return products;
    }

    // READ: one product by ID
    public Product findById(String id) {
        for (Product p : findAll()) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    // READ: search by name or category
    public List<Product> search(String keyword) {
        List<Product> results = new ArrayList<>();
        String key = keyword.toLowerCase();
        for (Product p : findAll()) {
            if (p.getName().toLowerCase().contains(key)
                    || p.getCategory().toLowerCase().contains(key)) {
                results.add(p);
            }
        }
        return results;
    }

    // UPDATE: replace the product with the same ID
    public boolean update(Product updated) {
        List<Product> all = findAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(updated.getId())) {
                all.set(i, updated);
                found = true;
                break;
            }
        }
        if (found) writeAll(all);
        return found;
    }

    // DELETE: remove the product with this ID
    public boolean delete(String id) {
        List<Product> all = findAll();
        boolean removed = all.removeIf(p -> p.getId().equals(id));
        if (removed) writeAll(all);
        return removed;
    }

    // Rewrites the whole file (used by update and delete)
    private void writeAll(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Product p : products) {
                writer.write(p.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not write products", e);
        }
    }

    // Turns one line of the file back into a Product object
    private Product parseLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 7) return null;

        String type = parts[0];
        String id = parts[1];
        String name = parts[2];
        String category = parts[3];
        double price = Double.parseDouble(parts[4]);
        int stock = Integer.parseInt(parts[5]);

        if (type.equals("PERISHABLE")) {
            return new PerishableProduct(id, name, category, price, stock, LocalDate.parse(parts[6]));
        } else if (type.equals("NON_PERISHABLE")) {
            return new NonPerishableProduct(id, name, category, price, stock, Integer.parseInt(parts[6]));
        }
        return null;
    }
}