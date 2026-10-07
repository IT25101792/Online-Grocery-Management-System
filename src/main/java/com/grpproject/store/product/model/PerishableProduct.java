package com.grpproject.store.product.model;

import java.time.LocalDate;

public class PerishableProduct extends Product {
    private LocalDate expiryDate;

    public PerishableProduct(String id, String name, String category,
                             double price, int stock, LocalDate expiryDate) {
        super(id, name, category, price, stock);
        this.expiryDate = expiryDate;
    }

    @Override
    public double getDiscountedPrice() {
        // 20% off if it expires within 3 days
        if (LocalDate.now().plusDays(3).isAfter(expiryDate)) {
            return getPrice() * 0.8;
        }
        return getPrice();
    }

    @Override
    public String getProductType() { return "PERISHABLE"; }

    @Override
    public String toFileString() {
        return "PERISHABLE|" + getId() + "|" + getName() + "|" + getCategory() + "|"
                + getPrice() + "|" + getStock() + "|" + expiryDate;
    }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}