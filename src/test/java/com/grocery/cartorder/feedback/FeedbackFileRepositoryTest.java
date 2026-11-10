package com.grocery.cartorder.feedback;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackFileRepositoryTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void productReviewSupportsCreateReadUpdateAndDelete() {
        String filePath = temporaryDirectory.resolve("feedback.txt").toString();
        FeedbackRepository repository = new FeedbackFileRepository(filePath);

        ProductReview review = new ProductReview(
                0, "U001", "Fresh and good quality", new Date(),
                FeedbackStatus.PENDING, "", "V001", 5, "Great vegetables");

        // CREATE
        repository.save(review);
        assertTrue(review.getFeedbackId() > 0);

        // READ
        Feedback saved = repository.findById(review.getFeedbackId());
        assertInstanceOf(ProductReview.class, saved);
        assertEquals("U001", saved.getCustomerId());

        // UPDATE
        saved.setComment("Updated review");
        repository.update(saved);
        assertEquals("Updated review",
                repository.findById(review.getFeedbackId()).getComment());

        // DELETE
        repository.delete(review.getFeedbackId());
        assertNull(repository.findById(review.getFeedbackId()));
    }

    @Test
    void serviceFeedbackIsSavedAndCanBeFoundByCustomer() {
        FeedbackRepository repository = new FeedbackFileRepository(
                temporaryDirectory.resolve("service-feedback.txt").toString());

        repository.save(new ServiceFeedback(
                0, "U002", "Delivery was on time", new Date(),
                FeedbackStatus.PENDING, "", "ORD-123", "DELIVERY"));

        List<Feedback> results = repository.findByCustomerId("U002");
        assertEquals(1, results.size());
        assertInstanceOf(ServiceFeedback.class, results.get(0));
        assertEquals("ORD-123", ((ServiceFeedback) results.get(0)).getOrderId());
    }
}
