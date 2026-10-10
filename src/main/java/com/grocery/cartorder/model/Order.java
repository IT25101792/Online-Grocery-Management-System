package com.grocery.cartorder.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private String orderId;
    private String customerId;
    private List<OrderItem> items;
    private double totalAmount;
    private LocalDateTime orderDate;
    private String status;

    public Order() {

        this.items = new ArrayList<>();

    }

    public Order(String orderId, String customerId,
                 List<OrderItem> items, double totalAmount,
                 LocalDateTime orderDate, String status) {

        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;

    }

    public String getOrderId() {

        return orderId;

    }

    public void setOrderId(String orderId) {

        this.orderId = orderId;

    }

    public String getCustomerId() {

        return customerId;

    }

    public void setCustomerId(String customerId) {

        this.customerId = customerId;

    }

    public List<OrderItem> getItems() {

        return items;

    }

    public void setItems(List<OrderItem> items) {

        this.items = items;

    }

    public double getTotalAmount() {

        return totalAmount;

    }

    public void setTotalAmount(double totalAmount) {

        this.totalAmount = totalAmount;

    }

    public LocalDateTime getOrderDate() {

        return orderDate;

    }

    public void setOrderDate(LocalDateTime orderDate) {

        this.orderDate = orderDate;

    }

    public String getStatus() {

        return status;

    }

    public void setStatus(String status) {

        this.status = status;

    }


}
