package com.grpproject.store.Delivery.Model;

import java.io.Serializable;
import java.util.Date;

public class Delivery implements Serializable {
    private static final long serialVersionUID = 1L;

    private int deliveryId;
    private int orderId;
    private String address;
    private String driverName;
    private DeliveryStatus status;
    private Date deliveryDate;

    public Delivery() {
        this.driverName = "Unassigned";
        this.status = DeliveryStatus.PENDING;
    }

    public Delivery(int deliveryId, int orderId, String address, String driverName, DeliveryStatus status, Date deliveryDate) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.address = address;
        this.driverName = (driverName != null && !driverName.trim().isEmpty()) ? driverName.trim() : "Unassigned";
        this.status = status != null ? status : DeliveryStatus.PENDING;
        this.deliveryDate = deliveryDate;
    }

    // Design Diagram Domain Methods
    public void assignDriver(String name) {
        this.driverName = name;
        this.status = DeliveryStatus.ASSIGNED;
    }

    public void markOutForDelivery() {
        this.status = DeliveryStatus.OUT_FOR_DELIVERY;
    }

    public void markDelivered() {
        this.status = DeliveryStatus.DELIVERED;
    }

    // Getters and Setters
    public int getDeliveryId() { return deliveryId; }
    public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }

    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }
}