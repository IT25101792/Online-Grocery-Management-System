package com.grpproject.store.product.model;

public class NonPerishableProduct extends Product {
    private String packagingType;

    public NonPerishableProduct(int productId, String name, String category, double price,
                                int stockQty, int reorderLevel, String packagingType) {
        super(productId, name, category, price, stockQty, reorderLevel);
        this.packagingType = packagingType;
    }

    @Override
    public double getDiscountedPrice() { return getPrice(); } // no discount

    @Override
    public String getProductType() { return "NON_PERISHABLE"; }

    @Override
    public String toFileString() {
        return "NON_PERISHABLE|" + getProductId() + "|" + getName() + "|" + getCategory() + "|"
                + getPrice() + "|" + getStockQty() + "|" + getReorderLevel() + "|" + packagingType;
    }

    public String getPackagingType() { return packagingType; }
    public void setPackagingType(String packagingType) { this.packagingType = packagingType; }
}