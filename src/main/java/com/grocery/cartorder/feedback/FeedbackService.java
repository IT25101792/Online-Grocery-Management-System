package com.grocery.cartorder.feedback;

import com.grocery.cartorder.model.Order;
import com.grocery.cartorder.model.OrderItem;
import com.grocery.cartorder.service.OrderService;
import com.grocery.cartorder.service.ProductStockService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/** Business rules for product reviews and service feedback. */
@Service
public class FeedbackService {

    private final FeedbackRepository repo;
    private final OrderService orderService;
    private final ProductStockService productStockService;

    public FeedbackService(FeedbackRepository repo,
                           OrderService orderService,
                           ProductStockService productStockService) {
        this.repo = repo;
        this.orderService = orderService;
        this.productStockService = productStockService;
    }

    /** UML operation: submitProductReview(customerId, productId, rating, comment). */
    public ProductReview submitProductReview(String customerId, String productId,
                                             int rating, String comment) {
        return submitProductReview(customerId, productId, rating, "Product review", comment);
    }

    public ProductReview submitProductReview(String customerId, String productId,
                                             int rating, String title, String comment) {
        validateText(customerId, "Customer ID");
        validateText(productId, "Product ID");
        validateText(comment, "Comment");

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        // The uploaded project does not yet contain ProductService. ProductStockService
        // is the existing inventory component and provides the product IDs in stock.txt.
        Map<String, Integer> stock = productStockService.getAllStock();
        if (!stock.containsKey(productId)) {
            throw new IllegalArgumentException("Product does not exist in the current inventory.");
        }

        if (!hasPurchased(customerId, productId)) {
            throw new IllegalArgumentException("You can review only a product in one of your orders.");
        }

        ProductReview review = new ProductReview(
                0, customerId, comment.trim(), new Date(),
                FeedbackStatus.PENDING, "", productId, rating, cleanTitle(title));
        repo.save(review);
        return review;
    }

    /** UML operation: submitServiceFeedback(customerId, orderId, comment). */
    public ServiceFeedback submitServiceFeedback(String customerId, String orderId, String comment) {
        return submitServiceFeedback(customerId, orderId, "GENERAL", comment);
    }

    public ServiceFeedback submitServiceFeedback(String customerId, String orderId,
                                                 String category, String comment) {
        validateText(customerId, "Customer ID");
        validateText(orderId, "Order ID");
        validateText(comment, "Comment");

        Order order = orderService.getOrderById(orderId);
        if (order == null || !customerId.equals(order.getCustomerId())) {
            throw new IllegalArgumentException("The order was not found for this customer.");
        }
        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalArgumentException("Feedback cannot be submitted for a cancelled order.");
        }

        ServiceFeedback feedback = new ServiceFeedback(
                0, customerId, comment.trim(), new Date(),
                FeedbackStatus.PENDING, "", orderId, cleanCategory(category));
        repo.save(feedback);
        return feedback;
    }

    public Feedback getFeedbackById(int id) {
        return repo.findById(id);
    }

    /** Returns approved product reviews only, because only approved feedback is public. */
    public List<ProductReview> getReviewsByProduct(String productId) {
        List<ProductReview> visibleReviews = new ArrayList<>();
        for (Feedback feedback : repo.findByProductId(productId)) {
            if (feedback.isVisible() && feedback instanceof ProductReview) {
                visibleReviews.add((ProductReview) feedback);
            }
        }
        return visibleReviews;
    }

    public List<Feedback> getFeedbackByCustomer(String customerId) {
        return repo.findByCustomerId(customerId);
    }

    public List<Feedback> getAllFeedback() {
        return repo.findAll();
    }

    public void updateFeedback(int id, String comment, int rating) {
        validateText(comment, "Comment");
        Feedback feedback = repo.findById(id);
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback not found.");
        }

        feedback.setComment(comment.trim());
        if (feedback instanceof ProductReview) {
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5.");
            }
            ((ProductReview) feedback).setRating(rating);
        }

        // Edited feedback returns to the moderation queue.
        feedback.setStatus(FeedbackStatus.PENDING);
        repo.update(feedback);
    }

    public void deleteFeedback(int id) {
        if (repo.findById(id) == null) {
            throw new IllegalArgumentException("Feedback not found.");
        }
        repo.delete(id);
    }

    public void moderate(int id, FeedbackStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Feedback status is required.");
        }
        Feedback feedback = repo.findById(id);
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback not found.");
        }
        feedback.setStatus(status);
        repo.update(feedback);
    }

    public void addAdminReply(int id, String reply) {
        validateText(reply, "Admin reply");
        Feedback feedback = repo.findById(id);
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback not found.");
        }
        feedback.addReply(reply.trim());
        repo.update(feedback);
    }

    public double getAverageRating(String productId) {
        List<ProductReview> reviews = getReviewsByProduct(productId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        for (ProductReview review : reviews) {
            total += review.getRating();
        }
        return (double) total / reviews.size();
    }

    /** Checks the existing orders.txt data through OrderService. */
    public boolean hasPurchased(String customerId, String productId) {
        if (customerId == null || productId == null) {
            return false;
        }

        List<Order> orders = orderService.getAllOrders();
        for (Order order : orders) {
            if (customerId.equals(order.getCustomerId())
                    && !"CANCELLED".equalsIgnoreCase(order.getStatus())
                    && order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    if (productId.equals(item.getProductId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    private String cleanTitle(String title) {
        return title == null || title.trim().isEmpty() ? "Product review" : title.trim();
    }

    private String cleanCategory(String category) {
        return category == null || category.trim().isEmpty() ? "GENERAL" : category.trim();
    }
}
