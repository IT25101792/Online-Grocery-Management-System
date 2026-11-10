package com.grpproject.store.Delivery.Repository;

import com.grpproject.store.Delivery.Model.Delivery;
import com.grpproject.store.Delivery.Model.DeliveryStatus;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class DeliveryFileRepository implements DeliveryRepository {

    private final String filePath = "src/data/deliveries.txt";

    public DeliveryFileRepository() {
        initFileStorage();
    }

    private synchronized void initFileStorage() {
        try {
            File file = new File(filePath);
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

    private List<Delivery> readFromFile() {
        List<Delivery> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        int deliveryId = Integer.parseInt(parts[0].trim());
                        int orderId = Integer.parseInt(parts[1].trim());
                        String address = parts[2].trim();
                        String driver = parts[3].trim();
                        DeliveryStatus status = DeliveryStatus.valueOf(parts[4].trim());
                        long dateMillis = Long.parseLong(parts[5].trim());
                        Date date = dateMillis == 0L ? null : new Date(dateMillis);

                        list.add(new Delivery(deliveryId, orderId, address, driver, status, date));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading delivery file: " + e.getMessage());
        }
        return list;
    }

    private void writeToFile(List<Delivery> deliveries) {
        initFileStorage();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE)) {
            for (Delivery d : deliveries) {
                if (d != null) {
                    long dateMillis = d.getDeliveryDate() != null ? d.getDeliveryDate().getTime() : 0L;
                    String line = d.getDeliveryId() + "," + d.getOrderId() + "," + d.getAddress() + ","
                            + d.getDriverName() + "," + d.getStatus().name() + "," + dateMillis;
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int generateNextId() {
        List<Delivery> deliveries = readFromFile();
        int maxId = 0;
        for (Delivery d : deliveries) {
            if (d.getDeliveryId() > maxId) {
                maxId = d.getDeliveryId();
            }
        }
        return maxId + 1;
    }

    @Override
    public synchronized void save(Delivery d) {
        List<Delivery> deliveries = readFromFile();
        if (d.getDeliveryId() == 0) {
            d.setDeliveryId(generateNextId());
        }
        deliveries.add(d);
        writeToFile(deliveries);
    }

    @Override
    public Delivery findById(int id) {
        return readFromFile().stream()
                .filter(d -> d.getDeliveryId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Delivery findByOrderId(int orderId) {
        return readFromFile().stream()
                .filter(d -> d.getOrderId() == orderId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Delivery> findAll() {
        return readFromFile();
    }

    @Override
    public synchronized void update(Delivery d) {
        List<Delivery> deliveries = readFromFile();
        for (int i = 0; i < deliveries.size(); i++) {
            if (deliveries.get(i).getDeliveryId() == d.getDeliveryId()) {
                deliveries.set(i, d);
                break;
            }
        }
        writeToFile(deliveries);
    }

    @Override
    public synchronized void delete(int id) {
        List<Delivery> deliveries = readFromFile();
        deliveries.removeIf(d -> d.getDeliveryId() == id);
        writeToFile(deliveries);
    }
}