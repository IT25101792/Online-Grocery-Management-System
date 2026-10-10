package com.grpproject.store.product.model;

import java.time.LocalDate;

public class PerishableProduct extends Product {
    private LocalDate expiryDate;

    public PerishableProduct(int productId, String name, String category, double price,
                             int stockQty, int reorderLevel, LocalDate expiryDate) {
        super(productId, name, category, price, stockQty, reorderLevel);
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
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
        return "PERISHABLE|" + getProductId() + "|" + getName() + "|" + getCategory() + "|"
                + getPrice() + "|" + getStockQty() + "|" + getReorderLevel() + "|" + expiryDate;
    }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}