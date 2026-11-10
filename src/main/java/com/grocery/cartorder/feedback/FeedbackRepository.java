package com.grocery.cartorder.feedback;

import java.util.List;

/** Data-access contract for feedback records. */
public interface FeedbackRepository {

    void save(Feedback feedback);

    Feedback findById(int id);

    List<Feedback> findByProductId(String productId);

    List<Feedback> findByCustomerId(String customerId);

    List<Feedback> findAll();

    void update(Feedback feedback);

    void delete(int id);
}
