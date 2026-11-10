package com.grocery.cartorder.feedback;

import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Stores feedback as pipe-separated text records in data/feedback.txt. */
@Repository
public class FeedbackFileRepository implements FeedbackRepository {

    private final String filePath;

    /** Constructor used by Spring in the running project. */
    public FeedbackFileRepository() {
        this("data/feedback.txt");
    }

    /** Constructor also allows tests to use a temporary file. */
    public FeedbackFileRepository(String filePath) {
        this.filePath = filePath;
        initializeFile();
    }

    private void initializeFile() {
        Path path = Paths.get(filePath);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create feedback file: " + filePath, e);
        }
    }

    private synchronized List<Feedback> readFromFile() {
        List<Feedback> feedbackList = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    Feedback feedback = parseLine(line);
                    if (feedback != null) {
                        feedbackList.add(feedback);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read feedback file.", e);
        }
        return feedbackList;
    }

    private synchronized void writeToFile(List<Feedback> feedbackList) {
        Path path = Paths.get(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            for (Feedback feedback : feedbackList) {
                writer.write(feedback.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to write feedback file.", e);
        }
    }

    private Feedback parseLine(String line) {
        String[] parts = line.split("\\|", -1);
        try {
            if (parts.length < 9) {
                return null;
            }

            int feedbackId = Integer.parseInt(parts[1]);
            String customerId = parts[2];
            String comment = parts[3];
            Date createdDate = new Date(Long.parseLong(parts[4]));
            FeedbackStatus status = FeedbackStatus.valueOf(parts[5]);
            String adminReply = parts[6];

            if ("PRODUCT".equals(parts[0]) && parts.length == 10) {
                return new ProductReview(
                        feedbackId,
                        customerId,
                        comment,
                        createdDate,
                        status,
                        adminReply,
                        parts[7],
                        Integer.parseInt(parts[8]),
                        parts[9]);
            }

            if ("SERVICE".equals(parts[0]) && parts.length == 9) {
                return new ServiceFeedback(
                        feedbackId,
                        customerId,
                        comment,
                        createdDate,
                        status,
                        adminReply,
                        parts[7],
                        parts[8]);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Skipping invalid feedback record: " + line);
        }
        return null;
    }

    private int generateNextId() {
        List<Feedback> feedbackList = readFromFile();
        int largestId = 0;
        for (Feedback feedback : feedbackList) {
            if (feedback.getFeedbackId() > largestId) {
                largestId = feedback.getFeedbackId();
            }
        }
        return largestId + 1;
    }

    @Override
    public synchronized void save(Feedback feedback) {
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback cannot be null.");
        }

        List<Feedback> feedbackList = readFromFile();
        if (feedback.getFeedbackId() <= 0) {
            feedback.setFeedbackId(generateNextId());
        } else if (findInList(feedbackList, feedback.getFeedbackId()) != null) {
            throw new IllegalArgumentException("Feedback ID already exists.");
        }

        feedbackList.add(feedback);
        writeToFile(feedbackList);
    }

    @Override
    public synchronized Feedback findById(int id) {
        for (Feedback feedback : readFromFile()) {
            if (feedback.getFeedbackId() == id) {
                return feedback;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Feedback> findByProductId(String productId) {
        List<Feedback> result = new ArrayList<>();
        for (Feedback feedback : readFromFile()) {
            if (feedback instanceof ProductReview) {
                ProductReview review = (ProductReview) feedback;
                if (review.getProductId().equals(productId)) {
                    result.add(review);
                }
            }
        }
        return result;
    }

    @Override
    public synchronized List<Feedback> findByCustomerId(String customerId) {
        List<Feedback> result = new ArrayList<>();
        for (Feedback feedback : readFromFile()) {
            if (feedback.getCustomerId().equals(customerId)) {
                result.add(feedback);
            }
        }
        return result;
    }

    @Override
    public synchronized List<Feedback> findAll() {
        return readFromFile();
    }

    @Override
    public synchronized void update(Feedback updatedFeedback) {
        if (updatedFeedback == null) {
            throw new IllegalArgumentException("Feedback cannot be null.");
        }

        List<Feedback> feedbackList = readFromFile();
        boolean found = false;
        for (int i = 0; i < feedbackList.size(); i++) {
            if (feedbackList.get(i).getFeedbackId() == updatedFeedback.getFeedbackId()) {
                feedbackList.set(i, updatedFeedback);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Feedback not found.");
        }
        writeToFile(feedbackList);
    }

    @Override
    public synchronized void delete(int id) {
        List<Feedback> feedbackList = readFromFile();
        boolean removed = feedbackList.removeIf(feedback -> feedback.getFeedbackId() == id);
        if (removed) {
            writeToFile(feedbackList);
        }
    }

    private Feedback findInList(List<Feedback> feedbackList, int id) {
        for (Feedback feedback : feedbackList) {
            if (feedback.getFeedbackId() == id) {
                return feedback;
            }
        }
        return null;
    }
}
