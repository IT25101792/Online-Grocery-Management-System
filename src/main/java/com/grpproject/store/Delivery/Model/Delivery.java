package com.grpproject.store.Delivery.Model;

import java.io.Serializable;

public class Delivery implements Serializable {
    private static final long serialVersionUID = 1L;

    private String deliveryID;
    private String orderID;
    private String customerName;
    private String deliveryAddress;
    private String deliveryStatus;
    private double deliveryFee;

    public Delivery() {
        this.deliveryID = "";
        this.orderID = "";
        this.customerName = "";
        this.deliveryAddress = "";
        this.deliveryStatus = "Pending";
        this.deliveryFee = 0.0;
    }

    public Delivery(String deliveryID, String orderID, String customerName, String deliveryAddress, String deliveryStatus, double deliveryFee) {
        this.deliveryID = deliveryID;
        this.orderID = orderID;
        this.customerName = customerName;
        this.deliveryAddress = deliveryAddress;
        this.deliveryStatus = deliveryStatus;
        this.deliveryFee = deliveryFee;
    }

    // Getters and Setters
    public String getDeliveryID() { return deliveryID; }
    public void setDeliveryID(String deliveryID) { this.deliveryID = deliveryID; }

    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }

    // Convert object to flat file storage line
    public String toFileString() {
        String safeID = deliveryID != null ? deliveryID.trim() : "";
        String safeOrder = orderID != null ? orderID.trim() : "";
        String safeName = customerName != null ? customerName.trim() : "";
        String safeAddress = deliveryAddress != null ? deliveryAddress.trim() : "";
        String safeStatus = deliveryStatus != null ? deliveryStatus.trim() : "Pending";

        return safeID + "," + safeOrder + "," + safeName + "," + safeAddress + "," + safeStatus + "," + deliveryFee;
    }

    // Convert file line back to Delivery object with flexible comma handling for addresses
    public static Delivery fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split(",");
        // Minimum required fields: ID, OrderID, Name, Address(at least 1 part), Status, Fee -> 6 parts minimum
        if (parts.length < 6) {
            return null;
        }

        try {
            String id = parts[0].trim();
            String order = parts[1].trim();
            String name = parts[2].trim();

            // Last element is always the delivery fee
            double fee = Double.parseDouble(parts[parts.length - 1].trim());

            // Second to last element is always the delivery status
            String status = parts[parts.length - 2].trim();

            // Reconstruct address in case the address itself contained commas
            StringBuilder addressBuilder = new StringBuilder();
            for (int i = 3; i < parts.length - 2; i++) {
                if (i > 3) {
                    addressBuilder.append(",");
                }
                addressBuilder.append(parts[i]);
            }
            String address = addressBuilder.toString().trim();

            return new Delivery(id, order, name, address, status, fee);
        } catch (Exception e) {
            // Prevents application crash if a data line is corrupted
            System.err.println("Error parsing delivery line: " + line);
            return null;
        }
    }
}