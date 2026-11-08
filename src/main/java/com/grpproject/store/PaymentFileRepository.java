package com.grpproject.store;

import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentFileRepository {

    private final Path filePath = Paths.get("data", "payment.txt");

    public PaymentFileRepository() {
        try {
            Files.createDirectories(filePath.getParent());

            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create payment.txt", e);
        }
    }

    public synchronized List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();

        try {
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    Payment payment = fromLine(line);
                    if (payment != null) {
                        payments.add(payment);
                    }
                }
            }
            return payments;
        } catch (IOException e) {
            throw new RuntimeException("Could not read payment.txt", e);
        }
    }

    public synchronized void save(Payment payment) {
        List<Payment> payments = findAll();

        // Replace an existing record with the same payment ID.
        payments.removeIf(existing ->
                existing.getPaymentId() != null
                        && existing.getPaymentId().equals(payment.getPaymentId()));

        payments.add(payment);
        writeAll(payments);
    }

    public synchronized void deleteById(String paymentId) {
        List<Payment> payments = findAll();
        payments.removeIf(payment -> paymentId.equals(payment.getPaymentId()));
        writeAll(payments);
    }

    private void writeAll(List<Payment> payments) {
        List<String> lines = new ArrayList<>();

        for (Payment payment : payments) {
            lines.add(toLine(payment));
        }

        try {
            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not write payment.txt", e);
        }
    }

    private String toLine(Payment payment) {
        return formatField(payment.getPaymentId()) + "|"
                + formatField(payment.getOrderId()) + "|"
                + formatField(payment.getCustomerName()) + "|"
                + formatField(payment.getPhoneNumber()) + "|"
                + formatField(payment.getItems()) + "|"
                + formatField(payment.getAmount()) + "|"
                + formatField(payment.getPaymentMethod()) + "|"
                + formatField(payment.getPaymentDate());
    }

    private Payment fromLine(String line) {
        String[] fields = line.split("\\|", -1);

        // Skip malformed or older-format lines.
        if (fields.length != 8) {
            return null;
        }

        Payment payment = new Payment();
        payment.setPaymentId(fields[0]);
        payment.setOrderId(fields[1]);
        payment.setCustomerName(fields[2]);
        payment.setPhoneNumber(fields[3]);
        payment.setItems(fields[4]);
        payment.setAmount(fields[5]);
        payment.setPaymentMethod(fields[6]);
        payment.setPaymentDate(fields[7]);
        return payment;
    }

    private String formatField(String value) {
        return value == null ? "" : value;
    }
}
