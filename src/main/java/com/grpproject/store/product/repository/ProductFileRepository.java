package com.grpproject.store.product.repository;

import com.grpproject.store.product.model.NonPerishableProduct;
import com.grpproject.store.product.model.PerishableProduct;
import com.grpproject.store.product.model.Product;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductFileRepository implements ProductRepository {

    private final String filePath = "data/products.txt";

    // Creates the data folder and file if they don't exist yet
    public ProductFileRepository() {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile(); // does nothing if it already exists
        } catch (IOException e) {
            throw new RuntimeException("Could not create products file", e);
        }
    }

    // CREATE
    @Override
    public void save(Product p) {
        List<Product> all = readFromFile();
        all.add(p);
        writeToFile(all);
    }

    // READ one
    @Override
    public Product findById(int id) {
        for (Product p : readFromFile()) {
            if (p.getProductId() == id) return p;
        }
        return null;
    }

    // READ all
    @Override
    public List<Product> findAll() {
        return readFromFile();
    }

    // UPDATE: replace the product with the same ID
    @Override
    public void update(Product updated) {
        List<Product> all = readFromFile();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getProductId() == updated.getProductId()) {
                all.set(i, updated);
                break;
            }
        }
        writeToFile(all);
    }

    // DELETE
    @Override
    public void delete(int id) {
        List<Product> all = readFromFile();
        all.removeIf(p -> p.getProductId() == id);
        writeToFile(all);
    }

    // Reads every line of the file and turns it into Product objects
    private List<Product> readFromFile() {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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

    // Rewrites the whole file from the list
    private void writeToFile(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Product p : products) {
                writer.write(p.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not write products", e);
        }
    }

    // One line: TYPE|productId|name|category|price|stockQty|reorderLevel|extra
    private Product parseLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 8) return null;

        try {
            String type = parts[0];
            int productId = Integer.parseInt(parts[1]);
            String name = parts[2];
            String category = parts[3];
            double price = Double.parseDouble(parts[4]);
            int stockQty = Integer.parseInt(parts[5]);
            int reorderLevel = Integer.parseInt(parts[6]);

            if (type.equals("PERISHABLE")) {
                return new PerishableProduct(productId, name, category, price,
                        stockQty, reorderLevel, LocalDate.parse(parts[7]));
            } else if (type.equals("NON_PERISHABLE")) {
                return new NonPerishableProduct(productId, name, category, price,
                        stockQty, reorderLevel, parts[7]);
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            return null; // skip a damaged line instead of crashing
        }
        return null;
    }
}