package com.grpproject.store.Delivery.Repository;

import com.grpproject.store.Delivery.Model.Delivery;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Repository handling file storage operations for deliveries.txt
@Repository
public class DeliveryRepository {

    private static final String FILE_PATH = "src/data/deliveries.txt";

    public DeliveryRepository() {
        initFileStorage();
    }

    private synchronized void initFileStorage() {
        try {
            File file = new File(FILE_PATH);
            File parentDir = file.getParentFile();

            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize storage file: " + e.getMessage());
        }
    }

    public synchronized boolean saveDelivery(Delivery delivery) {
        if (delivery == null || delivery.getDeliveryID() == null || delivery.getDeliveryID().trim().isEmpty()) {
            return false;
        }

        if (getDeliveryById(delivery.getDeliveryID()) != null) {
            return false;
        }

        initFileStorage();

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH), StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND)) {
            writer.write(delivery.toFileString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized List<Delivery> getAllDeliveries() {
        List<Delivery> deliveryList = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return deliveryList;
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Delivery delivery = Delivery.fromFileString(line);
                    if (delivery != null) {
                        deliveryList.add(delivery);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deliveryList;
    }

    public Delivery getDeliveryById(String deliveryID) {
        if (deliveryID == null || deliveryID.trim().isEmpty()) return null;

        List<Delivery> deliveries = getAllDeliveries();
        for (Delivery d : deliveries) {
            if (d.getDeliveryID() != null && d.getDeliveryID().trim().equalsIgnoreCase(deliveryID.trim())) {
                return d;
            }
        }
        return null;
    }

    public synchronized boolean updateDeliveryStatus(String deliveryID, String newStatus) {
        if (deliveryID == null || newStatus == null || deliveryID.trim().isEmpty()) return false;

        List<Delivery> deliveries = getAllDeliveries();
        boolean isUpdated = false;

        for (Delivery d : deliveries) {
            if (d.getDeliveryID() != null && d.getDeliveryID().trim().equalsIgnoreCase(deliveryID.trim())) {
                d.setDeliveryStatus(newStatus);
                isUpdated = true;
                break;
            }
        }

        return isUpdated && rewriteFile(deliveries);
    }

    public synchronized boolean updateDelivery(Delivery updatedDelivery) {
        if (updatedDelivery == null || updatedDelivery.getDeliveryID() == null || updatedDelivery.getDeliveryID().trim().isEmpty()) {
            return false;
        }

        List<Delivery> deliveries = getAllDeliveries();
        boolean isUpdated = false;

        for (int i = 0; i < deliveries.size(); i++) {
            Delivery existing = deliveries.get(i);
            if (existing.getDeliveryID() != null &&
                    existing.getDeliveryID().trim().equalsIgnoreCase(updatedDelivery.getDeliveryID().trim())) {
                deliveries.set(i, updatedDelivery);
                isUpdated = true;
                break;
            }
        }

        return isUpdated && rewriteFile(deliveries);
    }

    public synchronized boolean deleteDelivery(String deliveryID) {
        if (deliveryID == null || deliveryID.trim().isEmpty()) return false;

        List<Delivery> deliveries = getAllDeliveries();
        boolean isDeleted = deliveries.removeIf(d ->
                d.getDeliveryID() != null && d.getDeliveryID().trim().equalsIgnoreCase(deliveryID.trim())
        );

        return isDeleted && rewriteFile(deliveries);
    }

    private boolean rewriteFile(List<Delivery> deliveries) {
        initFileStorage();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH), StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE)) {
            for (Delivery d : deliveries) {
                if (d != null) {
                    writer.write(d.toFileString());
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}