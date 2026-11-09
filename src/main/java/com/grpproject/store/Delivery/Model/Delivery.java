package com.grpproject.store.Delivery.Model;

import java.io.Serializable;

public class Delivery implements Serializable {
    private static final long serialVersionUID = 1L;

    private String deliveryID;
    private String orderID;
    private String customerName;
    private String deliveryAddress;
    private String driverName;
    private String driverContact;
    private String deliveryStatus;
    private double deliveryFee;

    public Delivery() {
        this.deliveryID = "";
        this.orderID = "";
        this.customerName = "";
        this.deliveryAddress = "";
        this.driverName = "Unassigned";
        this.driverContact = "N/A";
        this.deliveryStatus = "Pending";
        this.deliveryFee = 0.0;
    }

    public Delivery(String deliveryID, String orderID, String customerName, String deliveryAddress,
                    String driverName, String driverContact, String deliveryStatus, double deliveryFee) {
        this.deliveryID = deliveryID;
        this.orderID = orderID;
        this.customerName = customerName;
        this.deliveryAddress = deliveryAddress;
        this.driverName = (driverName != null && !driverName.trim().isEmpty()) ? driverName.trim() : "Unassigned";
        this.driverContact = (driverContact != null && !driverContact.trim().isEmpty()) ? driverContact.trim() : "N/A";
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

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverContact() { return driverContact; }
    public void setDriverContact(String driverContact) { this.driverContact = driverContact; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }

    // Convert object to flat file storage line (8 fields)
    public String toFileString() {
        String safeID = deliveryID != null ? deliveryID.trim() : "";
        String safeOrder = orderID != null ? orderID.trim() : "";
        String safeName = customerName != null ? customerName.trim() : "";
        String safeAddress = deliveryAddress != null ? deliveryAddress.trim() : "";
        String safeDriver = driverName != null ? driverName.trim() : "Unassigned";
        String safeContact = driverContact != null ? driverContact.trim() : "N/A";
        String safeStatus = deliveryStatus != null ? deliveryStatus.trim() : "Pending";

        return safeID + "," + safeOrder + "," + safeName + "," + safeAddress + "," + safeDriver + "," + safeContact + "," + safeStatus + "," + deliveryFee;
    }

    // Convert flat file line back to Delivery object
    public static Delivery fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split(",");
        if (parts.length < 8) {
            return null;
        }

        try {
            String id = parts[0].trim();
            String order = parts[1].trim();
            String name = parts[2].trim();
            String address = parts[3].trim();
            String driver = parts[4].trim();
            String contact = parts[5].trim();
            String status = parts[6].trim();
            double fee = Double.parseDouble(parts[7].trim());

            return new Delivery(id, order, name, address, driver, contact, status, fee);
        } catch (Exception e) {
            System.err.println("Error parsing delivery line: " + line);
            return null;
        }
    }
}