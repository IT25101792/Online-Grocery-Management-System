package com.grpproject.store.Delivery.Model;

public class Delivery {
    private String deliveryID;
    private String orderID;
    private String customerName;
    private String deliveryAddress;
    private String deliveryStatus;
    private double deliveryFee;

    public Delivery() {}

    public Delivery(String deliveryID, String orderID, String customerName, String deliveryAddress, String deliveryStatus, double deliveryFee) {
        this.deliveryID = deliveryID;
        this.orderID = orderID;
        this.customerName = customerName;
        this.deliveryAddress = deliveryAddress;
        this.deliveryStatus = deliveryStatus;
        this.deliveryFee = deliveryFee;
    }

    public String getDeliveryID() { return deliveryID; }
    public void setDeliveryID(String deliveryID) { this.deliveryID = deliveryID; }

    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    // FIXED: Returns field instead of calling getter recursively
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public double getDeliveryFee() { return deliveryFee; }
    // FIXED: Accepts double deliveryFee parameter
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }

    public String toFileString() {
        return deliveryID + "," + orderID + "," + customerName + "," + deliveryAddress + "," + deliveryStatus + "," + deliveryFee;
    }

    public static Delivery fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 6) {
            return new Delivery(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim(),
                    Double.parseDouble(parts[5].trim())
            );
        }
        return null;
    }
}