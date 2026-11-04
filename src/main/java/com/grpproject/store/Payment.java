package com.grpproject.store;

public class Payment {
    private String paymentId;
    private String orderId;
    private String customerName;
    private double amount;
    private paymenttype type;
    private paymentStatus status;
    private String date;

    public Payment() {}

    public Payment(String paymentId, String orderId, String customerName, double amount, paymenttype type, paymentStatus status, String date) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.date = date;
    }

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public paymenttype getType() { return type; }
    public void setType(paymenttype type) { this.type = type; }

    public paymentStatus getStatus() { return status; }
    public void setStatus(paymentStatus status) { this.status = status; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    // Converts object into a single line of text for the text file
    public String toCSV() {
        return paymentId + "," + orderId + "," + customerName + "," + amount + "," + type + "," + status + "," + date;
    }

    // Reads a line of text from the file and converts it back into a Payment object
    public static Payment fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 7) return null;
        return new Payment(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                Double.parseDouble(parts[3].trim()),
                paymenttype.valueOf(parts[4].trim()),
                paymentStatus.valueOf(parts[5].trim()),
                parts[6].trim()
        );
    }
}