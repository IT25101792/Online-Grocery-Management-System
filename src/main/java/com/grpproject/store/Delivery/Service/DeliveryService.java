package com.grpproject.store.Delivery.Service;

import com.grpproject.store.Delivery.Model.Delivery;
import com.grpproject.store.Delivery.Model.DeliveryStatus;
import com.grpproject.store.Delivery.Repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DeliveryService {

    private final DeliveryRepository repo;

    @Autowired
    public DeliveryService(DeliveryRepository repo) {
        this.repo = repo;
    }

    public Delivery createDelivery(int orderId, String address) {
        Delivery d = new Delivery();
        d.setOrderId(orderId);
        d.setAddress(address);
        d.setStatus(DeliveryStatus.PENDING);
        d.setDeliveryDate(new Date());
        repo.save(d);
        return d;
    }

    public Delivery getDeliveryById(int id) {
        return repo.findById(id);
    }

    public Delivery getDeliveryByOrder(int orderId) {
        return repo.findByOrderId(orderId);
    }

    public List<Delivery> getAllDeliveries() {
        return repo.findAll();
    }

    public void assignDriver(int id, String driver) {
        Delivery d = repo.findById(id);
        if (d != null) {
            d.assignDriver(driver);
            repo.update(d);
        }
    }

    public void updateStatus(int id, DeliveryStatus status) {
        Delivery d = repo.findById(id);
        if (d != null) {
            d.setStatus(status);
            repo.update(d);
        }
    }

    public void reschedule(int id, Date newDate) {
        Delivery d = repo.findById(id);
        if (d != null) {
            d.setDeliveryDate(newDate);
            repo.update(d);
        }
    }

    public void deleteDelivery(int id) {
        repo.delete(id);
    }
}