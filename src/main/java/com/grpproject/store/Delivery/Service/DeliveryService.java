package com.grpproject.store.Delivery.Service;

import com.grpproject.store.Delivery.Model.Delivery;
import com.grpproject.store.Delivery.Repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public boolean createDelivery(Delivery delivery) {
        if (delivery == null || delivery.getDeliveryID() == null || delivery.getDeliveryID().trim().isEmpty()) {
            return false;
        }

        String trimmedID = delivery.getDeliveryID().trim();
        delivery.setDeliveryID(trimmedID);

        if (deliveryRepository.getDeliveryById(trimmedID) != null) {
            return false;
        }

        if (delivery.getDeliveryStatus() == null || delivery.getDeliveryStatus().trim().isEmpty()) {
            delivery.setDeliveryStatus("Pending");
        } else {
            delivery.setDeliveryStatus(delivery.getDeliveryStatus().trim());
        }

        if (delivery.getDeliveryFee() < 0) {
            delivery.setDeliveryFee(0.0);
        }

        return deliveryRepository.saveDelivery(delivery);
    }

    public List<Delivery> getAllDeliveries() {
        List<Delivery> deliveries = deliveryRepository.getAllDeliveries();
        return deliveries != null ? deliveries : new ArrayList<>();
    }

    public Delivery getDeliveryById(String deliveryID) {
        if (deliveryID == null || deliveryID.trim().isEmpty()) {
            return null;
        }
        return deliveryRepository.getDeliveryById(deliveryID.trim());
    }

    public boolean updateDeliveryStatus(String deliveryID, String newStatus) {
        if (deliveryID == null || newStatus == null || deliveryID.trim().isEmpty() || newStatus.trim().isEmpty()) {
            return false;
        }

        String cleanID = deliveryID.trim();
        String cleanStatus = newStatus.trim();

        Delivery existingDelivery = deliveryRepository.getDeliveryById(cleanID);
        if (existingDelivery == null) {
            return false;
        }

        return deliveryRepository.updateDeliveryStatus(cleanID, cleanStatus);
    }

    public boolean updateDelivery(Delivery delivery) {
        if (delivery == null || delivery.getDeliveryID() == null || delivery.getDeliveryID().trim().isEmpty()) {
            return false;
        }

        String cleanID = delivery.getDeliveryID().trim();
        delivery.setDeliveryID(cleanID);

        Delivery existingDelivery = deliveryRepository.getDeliveryById(cleanID);
        if (existingDelivery == null) {
            return false;
        }

        if (delivery.getDeliveryStatus() == null || delivery.getDeliveryStatus().trim().isEmpty()) {
            delivery.setDeliveryStatus(existingDelivery.getDeliveryStatus());
        } else {
            delivery.setDeliveryStatus(delivery.getDeliveryStatus().trim());
        }

        if (delivery.getDeliveryFee() < 0) {
            delivery.setDeliveryFee(existingDelivery.getDeliveryFee());
        }

        return deliveryRepository.updateDelivery(delivery);
    }

    public boolean deleteDelivery(String deliveryID) {
        if (deliveryID == null || deliveryID.trim().isEmpty()) {
            return false;
        }
        return deliveryRepository.deleteDelivery(deliveryID.trim());
    }
}