package com.grpproject.store;

import org.springframework.stereotype.Repository;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentFileRepository {
    private final String FILE_PATH = "payments.txt";

    public PaymentFileRepository() {
        File file = new File(FILE_PATH);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CREATE: Save a new payment to the text file
    public synchronized void save(Payment payment) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(payment.toCSV());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // READ: Get all payments from the text file
    public synchronized List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Payment p = Payment.fromCSV(line);
                    if (p != null) payments.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return payments;
    }

    // DELETE: Delete a payment by its ID from the text file
    public synchronized boolean deleteById(String paymentId) {
        List<Payment> payments = findAll();
        boolean removed = payments.removeIf(p -> p.getPaymentId().equalsIgnoreCase(paymentId));
        if (removed) {
            saveAll(payments);
        }
        return removed;
    }

    private void saveAll(List<Payment> payments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Payment p : payments) {
                writer.write(p.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}