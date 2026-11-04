package com.grpproject.store.Delivery.Service;

import com.grpproject.store.Delivery.Model.Delivery;
import com.grpproject.store.Delivery.Repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    // 1. Process and validate creating a new delivery
    public boolean createDelivery(Delivery delivery) {
        if (delivery == null || delivery.getDeliveryID() == null || delivery.getDeliveryID().isEmpty()) {
            return false; // Basic validation
        }
        return deliveryRepository.saveDelivery(delivery);
    }

    // 2. Retrieve all delivery records
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.getAllDeliveries();
    }

    // 3. Search for a delivery by ID
    public Delivery getDeliveryById(String deliveryID) {
        if (deliveryID == null || deliveryID.trim().isEmpty()) {
            return null;
        }
        return deliveryRepository.getDeliveryById(deliveryID);
    }

    // 4. Update status with business logic validation
    public boolean updateDeliveryStatus(String deliveryID, String newStatus) {
        if (deliveryID == null || newStatus == null || newStatus.trim().isEmpty()) {
            return false;
        }

        Delivery existingDelivery = deliveryRepository.getDeliveryById(deliveryID);
        if (existingDelivery == null) {
            return false;
        }

        return deliveryRepository.updateDeliveryStatus(deliveryID, newStatus);
    }
}