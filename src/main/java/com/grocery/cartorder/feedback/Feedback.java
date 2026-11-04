package com.grocery.cartorder.feedback;

import java.util.Date;

/**
 * Common information shared by product reviews and service feedback.
 * Product/order/customer IDs are Strings here because the existing grocery
 * project uses IDs such as V001, U001 and ORD-123.
 */
public abstract class Feedback {

    private int feedbackId;
    private String customerId;
    private String comment;
    private Date createdDate;
    private FeedbackStatus status;
    private String adminReply;

    public Feedback(int feedbackId, String customerId, String comment,
                    Date createdDate, FeedbackStatus status, String adminReply) {
        this.feedbackId = feedbackId;
        this.customerId = customerId;
        this.comment = comment;
        this.createdDate = createdDate == null ? new Date() : new Date(createdDate.getTime());
        this.status = status == null ? FeedbackStatus.PENDING : status;
        this.adminReply = adminReply == null ? "" : adminReply;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedDate() {
        return new Date(createdDate.getTime());
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate == null ? new Date() : new Date(createdDate.getTime());
    }

    public FeedbackStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    public String getAdminReply() {
        return adminReply;
    }

    public void setAdminReply(String adminReply) {
        this.adminReply = adminReply == null ? "" : adminReply;
    }

    public boolean isVisible() {
        return status == FeedbackStatus.APPROVED;
    }

    public void addReply(String reply) {
        this.adminReply = reply == null ? "" : reply;
    }

    /** Convert this object into one line for data/feedback.txt. */
    public abstract String toFileString();

    protected String safeFileValue(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('|', '/')
                .replace('\r', ' ')
                .replace('\n', ' ');
    }
}
