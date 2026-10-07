package com.grpproject.store.product.model;

public class NonPerishableProduct extends Product {
    private int shelfLifeMonths;

    public NonPerishableProduct(String id, String name, String category,
                                double price, int stock, int shelfLifeMonths) {
        super(id, name, category, price, stock);
        this.shelfLifeMonths = shelfLifeMonths;
    }

    @Override
    public double getDiscountedPrice() { return getPrice(); }

    @Override
    public String getProductType() { return "NON_PERISHABLE"; }

    @Override
    public String toFileString() {
        return "NON_PERISHABLE|" + getId() + "|" + getName() + "|" + getCategory() + "|"
                + getPrice() + "|" + getStock() + "|" + shelfLifeMonths;
    }

    public int getShelfLifeMonths() { return shelfLifeMonths; }
    public void setShelfLifeMonths(int shelfLifeMonths) { this.shelfLifeMonths = shelfLifeMonths; }
}