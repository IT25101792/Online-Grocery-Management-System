package com.grpproject.store.Delivery.Model;

//encapsulating attributes to private
public class Delivery {
    private String deliveryID;
    private String orderID;
    private String customerName;
    private String deliveryAddress;
    private String deliveryStatus;
    private double deliveryFee;

    //initializing default constructor
    public Delivery() {

    }

    //Parameterized Constructor
    public Delivery(String deliveryId, String orderID, String customerName, String deliveryAddress, String deliveryStatus, double deliveryFee) {

        this.deliveryID = deliveryId;
        this.orderID = orderID;
        this.customerName = customerName;
        this.deliveryAddress = deliveryAddress;
        this.deliveryStatus = deliveryStatus;
        this.deliveryFee = deliveryFee;
    }

    public String getdeliveryID(){
        return getdeliveryID();
    }
    public void setDeliveryID(){
        this.deliveryID = deliveryID;
    }
    public String getOrderID(){
        return getOrderID();
    }
    public void setOrderID(){
        this.orderID = orderID;
    }
    public String getCustomerName(){
        return customerName;
    }
    public void setCustomerName(){
        this.customerName = customerName;
    }
}
