package com.grpproject.store.product.model;

public abstract class Product {
    private int productId;
    private String name;
    private String category;
    private double price;
    private int stockQty;
    private int reorderLevel;

    public Product(int productId, String name, String category,
                   double price, int stockQty, int reorderLevel) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQty = stockQty;
        this.reorderLevel = reorderLevel;
    }

    // Each child class must implement these (abstraction + polymorphism)
    public abstract double getDiscountedPrice();
    public abstract String getProductType();
    public abstract String toFileString();

    public boolean isLowStock() {
        return stockQty <= reorderLevel;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStockQty() { return stockQty; }
    public void setStockQty(int stockQty) { this.stockQty = stockQty; }
    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
}