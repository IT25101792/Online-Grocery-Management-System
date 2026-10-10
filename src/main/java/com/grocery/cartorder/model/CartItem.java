package com.grocery.cartorder.model;

public class CartItem {

    private String cartItemId;
    private String productId;
    private String productName;
    private double price;
    private int quantity;

    public CartItem() {
    }

    public CartItem(String cartItemId, String productId,
                    String productName, double price, int quantity) {

        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;

    }

    public String getCartItemId() {

        return cartItemId;

    }

    public void setCartItemId(String cartItemId) {

        this.cartItemId = cartItemId;

    }

    public String getProductId() {

        return productId;

    }

    public void setProductId(String productId) {

        this.productId = productId;

    }

    public String getProductName() {

        return productName;

    }

    public void setProductName(String productName) {

        this.productName = productName;

    }

    public double getPrice(){

        return price;

    }

    public void setPrice(double price){

        if(price < 0){

            throw new IllegalArgumentException("Price Can not be Negative");

        } else {

            this.price = price;

        }

    }

    public int getQuantity(){

        return quantity;

    }

    public void setQuantity(int quantity){

        if(quantity <= 0){

            throw new IllegalArgumentException("Quantity must be Greater than zero");

        } else {

            this.quantity = quantity;

        }

    }

    public double getSubtotal() {

        return price * quantity;

    }



}
