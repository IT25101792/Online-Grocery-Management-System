package com.grpproject.store.Delivery.Repository;

import com.grpproject.store.Delivery.Model.Delivery;
import java.util.List;

public interface DeliveryRepository {
    void save(Delivery d);
    Delivery findById(int id);
    Delivery findByOrderId(int orderId);
    List<Delivery> findAll();
    void update(Delivery d);
    void delete(int id);
}