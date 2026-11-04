package com.grpproject.store.Delivery.Repository;

import com.grpproject.store.Delivery.Model.Delivery;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DeliveryRepository {

    // Path to the text file where delivery data is saved
    private static final String FILE_PATH = "deliveries.txt";

    // Append a new delivery record to the text file
    public boolean saveDelivery(Delivery delivery) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(delivery.toFileString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Read all delivery records from the text file
    public List<Delivery> getAllDeliveries() {
        List<Delivery> deliveryList = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return deliveryList; // Returns empty list if file doesn't exist yet
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
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

    //Search for a specific delivery by deliveryID
    public Delivery getDeliveryById(String deliveryID) {
        List<Delivery> deliveries = getAllDeliveries();
        for (Delivery d : deliveries) {
            if (d.getDeliveryID().equalsIgnoreCase(deliveryID)) {
                return d;
            }
        }
        return null;
    }

    //Update the delivery status (or details) of a record
    public boolean updateDeliveryStatus(String deliveryID, String newStatus) {
        List<Delivery> deliveries = getAllDeliveries();
        boolean isUpdated = false;

        for (Delivery d : deliveries) {
            if (d.getDeliveryID().equalsIgnoreCase(deliveryID)) {
                d.setDeliveryStatus(newStatus);
                isUpdated = true;
                break;
            }
        }

        if (isUpdated) {
            return rewriteFile(deliveries);
        }
        return false;
    }

    // Helper method to overwrite the file with the updated list
    private boolean rewriteFile(List<Delivery> deliveries) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Delivery d : deliveries) {
                writer.write(d.toFileString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
