package com.grpproject.store;

public class Payment {
    private String paymentId;
    private String orderId;
    private String customerName;
    private String phone;
    private String itemsSummary;
    private double amount;
    private paymenttype type;
    private paymentStatus status;
    private String date;

    public Payment() {}

    public Payment(String paymentId, String orderId, String customerName, String phone, String itemsSummary, double amount, paymenttype type, paymentStatus status, String date) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.customerName = customerName;
        this.phone = phone;
        this.itemsSummary = itemsSummary;
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

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getItemsSummary() { return itemsSummary; }
    public void setItemsSummary(String itemsSummary) { this.itemsSummary = itemsSummary; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public paymenttype getType() { return type; }
    public void setType(paymenttype type) { this.type = type; }

    public paymentStatus getStatus() { return status; }
    public void setStatus(paymentStatus status) { this.status = status; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    // Matches your UI standard: PaymentID,OrderId,CustomerName,Phone,ItemsSummary,Amount,Method,Status,Date
    public String toCSV() {
        return paymentId + "," + orderId + "," + customerName + "," + phone + "," + itemsSummary + "," + amount + "," + type + "," + status + "," + date;
    }

    public static Payment fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 9) return null;
        return new Payment(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                parts[4].trim(),
                Double.parseDouble(parts[5].trim()),
                paymenttype.valueOf(parts[6].trim()),
                paymentStatus.valueOf(parts[7].trim()),
                parts[8].trim()
        );
    }
}