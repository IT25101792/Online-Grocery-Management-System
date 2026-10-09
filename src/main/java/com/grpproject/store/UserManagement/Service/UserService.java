package com.grpproject.store.UserManagement.Service;




import com.grpproject.store.UserManagement.Model.Customer;
import com.grpproject.store.UserManagement.Model.User;
import com.grpproject.store.UserManagement.Repository.UserFileRepository;
import com.grpproject.store.UserManagement.Repository.UserRepository;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserService {

    private final UserRepository repo;

    public UserService() {
        this(new UserFileRepository());
    }

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    // Register a new customer
    public void registerCustomer(Customer customer) {

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer cannot be null");
        }

        if (customer.getName() == null
                || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Name is required");
        }

        if (customer.getEmail() == null
                || !Pattern.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                customer.getEmail().trim())) {
            throw new IllegalArgumentException(
                    "Please enter a valid email");
        }

        if (isEmailTaken(customer.getEmail())) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        if (customer.getPassword() == null
                || customer.getPassword().length() < 6) {
            throw new IllegalArgumentException(
                    "Password must contain at least 6 characters");
        }

        if (customer.getAddress() == null
                || customer.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Address is required");
        }

        if (customer.getPhone() == null
                || customer.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Phone number is required");
        }

        UserFileRepository fileRepo =
                repo instanceof UserFileRepository
                        ? (UserFileRepository) repo : null;

        if (customer.getUserId() <= 0) {
            if (fileRepo != null) {
                customer.setUserId(fileRepo.generateNextId());
            } else {
                int nextId = repo.findAll().stream()
                        .mapToInt(User::getUserId)
                        .max()
                        .orElse(0) + 1;
                customer.setUserId(nextId);
            }
        } else if (repo.findById(customer.getUserId()) != null) {
            throw new IllegalArgumentException(
                    "User ID already exists");
        }

        repo.save(customer);
    }

    // Authenticate a user using email and password
    public User authenticate(String email, String password) {

        if (email == null || password == null) {
            return null;
        }

        User user = repo.findByEmail(email.trim());

        if (user != null && user.login(password)) {
            return user;
        }

        return null;
    }

    // Find a user by ID
    public User getUserById(int id) {
        return repo.findById(id);
    }

    // Retrieve all customers, excluding admins
    public List<Customer> getAllCustomers() {
        return repo.findAll().stream()
                .filter(user -> user instanceof Customer)
                .map(user -> (Customer) user)
                .collect(Collectors.toList());
    }

    // Update customer profile
    public void updateProfile(Customer customer) {

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer cannot be null");
        }

        User existing = repo.findById(customer.getUserId());

        if (!(existing instanceof Customer)) {
            throw new IllegalArgumentException(
                    "Customer not found");
        }

        User emailOwner = repo.findByEmail(customer.getEmail());

        if (emailOwner != null
                && emailOwner.getUserId() != customer.getUserId()) {
            throw new IllegalArgumentException(
                    "Email is already used by another user");
        }

        repo.update(customer);
    }

    // Change password after verifying the old password
    public void changePassword(int id, String oldPassword,
                               String newPassword) {

        User user = repo.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!user.login(oldPassword)) {
            throw new IllegalArgumentException(
                    "Current password is incorrect");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException(
                    "New password must contain at least 6 characters");
        }

        user.setPassword(newPassword);
        repo.update(user);
    }

    // Delete a user account
    public void deleteUser(int id) {

        if (repo.findById(id) == null) {
            throw new IllegalArgumentException("User not found");
        }

        repo.delete(id);
    }

    // Check whether an email is already registered
    public boolean isEmailTaken(String email) {

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return repo.findByEmail(email.trim()) != null;
    }
}

