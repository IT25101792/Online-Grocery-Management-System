package com.grpproject.store.UserManagement.Repository;



import com.grpproject.store.UserManagement.Model.Admin;
import com.grpproject.store.UserManagement.Model.Customer;
import com.grpproject.store.UserManagement.Model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class UserFileRepository implements UserRepository {

    private final String filePath;

    public UserFileRepository() {
        this("data/users.txt");
    }

    public UserFileRepository(String filePath) {
        this.filePath = filePath;
    }

    private List<User> readFromFile() {
        List<User> users = new ArrayList<>();
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            return users;
        }

        try {
            for (String line : Files.readAllLines(path)) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                int id = Integer.parseInt(data[0]);
                String type = data[1];
                String name = data[2];
                String email = data[3];
                String password = data[4];

                if (type.equals("Customer") && data.length >= 7) {
                    users.add(new Customer(
                            id, name, email, password,
                            data[5], data[6]
                    ));
                } else if (type.equals("Admin") && data.length >= 6) {
                    users.add(new Admin(
                            id, name, email, password, data[5]
                    ));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read users file", e);
        }

        return users;
    }

    private void writeToFile(List<User> users) {
        Path path = Paths.get(filePath);

        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            List<String> lines = new ArrayList<>();

            for (User user : users) {
                lines.add(user.toFileString());
            }

            Files.write(
                    path,
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (IOException e) {
            throw new RuntimeException("Unable to save users file", e);
        }
    }

    public int generateNextId() {
        return readFromFile().stream()
                .mapToInt(User::getUserId)
                .max()
                .orElse(0) + 1;
    }

    @Override
    public void save(User user) {
        List<User> users = readFromFile();
        users.add(user);
        writeToFile(users);
    }

    @Override
    public User findById(int id) {
        return readFromFile().stream()
                .filter(user -> user.getUserId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return readFromFile().stream()
                .filter(user -> user.getEmail()
                        .equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return readFromFile();
    }


    @Override
    public void update(User updatedUser) {
        List<User> users = readFromFile();

        for (int i = 0; i < users.size(); i++) {

            if (users.get(i).getUserId() == updatedUser.getUserId()) {

                // Keep the existing password
                updatedUser.setPassword(users.get(i).getPassword());

                users.set(i, updatedUser);
                writeToFile(users);
                return;
            }
        }

        throw new IllegalArgumentException("User not found");
    }

    @Override
    public void delete(int id) {
        List<User> users = readFromFile();

        boolean removed = users.removeIf(
                user -> user.getUserId() == id
        );

        if (removed) {
            writeToFile(users);
        }
    }
}
