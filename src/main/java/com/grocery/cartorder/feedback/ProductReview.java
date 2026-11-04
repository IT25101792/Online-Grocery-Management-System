package com.grocery.cartorder.feedback;

import java.util.Date;

/** A customer's review of a product they have ordered. */
public class ProductReview extends Feedback {

    private String productId;
    private int rating;
    private String title;

    public ProductReview(int feedbackId, String customerId, String comment,
                         Date createdDate, FeedbackStatus status, String adminReply,
                         String productId, int rating, String title) {
        super(feedbackId, customerId, comment, createdDate, status, adminReply);
        this.productId = productId;
        this.rating = rating;
        this.title = title;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isValidRating() {
        return rating >= 1 && rating <= 5;
    }

    @Override
    public String toFileString() {
        return "PRODUCT|" + getFeedbackId()
                + "|" + safeFileValue(getCustomerId())
                + "|" + safeFileValue(getComment())
                + "|" + getCreatedDate().getTime()
                + "|" + getStatus()
                + "|" + safeFileValue(getAdminReply())
                + "|" + safeFileValue(productId)
                + "|" + rating
                + "|" + safeFileValue(title);
    }
}
