package com.grpproject.store.product.model;

public abstract class Product {
    private String id;
    private String name;
    private String category;
    private double price;
    private int stock;

    public Product(String id, String name, String category, double price, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    // Each child class must implement these (polymorphism)
    public abstract double getDiscountedPrice();
    public abstract String getProductType();
    public abstract String toFileString();

    public String displayDetails() {
        return name + " (" + category + ") - Rs." + getDiscountedPrice() + " | Stock: " + stock;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}