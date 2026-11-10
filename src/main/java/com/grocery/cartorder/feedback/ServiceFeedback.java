package com.grocery.cartorder.feedback;

import java.util.Date;

/** Feedback about the service received for a particular order. */
public class ServiceFeedback extends Feedback {

    private String orderId;
    private String category;

    public ServiceFeedback(int feedbackId, String customerId, String comment,
                           Date createdDate, FeedbackStatus status, String adminReply,
                           String orderId, String category) {
        super(feedbackId, customerId, comment, createdDate, status, adminReply);
        this.orderId = orderId;
        this.category = category;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toFileString() {
        return "SERVICE|" + getFeedbackId()
                + "|" + safeFileValue(getCustomerId())
                + "|" + safeFileValue(getComment())
                + "|" + getCreatedDate().getTime()
                + "|" + getStatus()
                + "|" + safeFileValue(getAdminReply())
                + "|" + safeFileValue(orderId)
                + "|" + safeFileValue(category);
    }
}
