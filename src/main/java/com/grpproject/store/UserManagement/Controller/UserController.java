package com.grpproject.store.UserManagement.Controller;




import com.grpproject.store.UserManagement.Model.Customer;
import com.grpproject.store.UserManagement.Model.User;
import com.grpproject.store.UserManagement.Service.UserService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController() {
        this.service = new UserService();
    }

    // Show registration endpoint
    @GetMapping("/register")
    public String showRegisterForm() {
        return "Registration endpoint is ready.";
    }

    // Register a new customer
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody Customer customer) {
        try {
            service.registerCustomer(customer);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Registration successful. User ID: "
                            + customer.getUserId());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // Show login endpoint
    @GetMapping("/login")
    public String showLoginForm() {
        return "Login endpoint is ready.";
    }

    // Login using email and password
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request) {

        if (request == null) {
            return ResponseEntity.badRequest()
                    .body("Login details are required.");
        }

        User user = service.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password.");
        }

        return ResponseEntity.ok(
                "Login successful. Welcome, " + user.getName()
        );
    }

    // Logout endpoint
    @PostMapping("/logout")
    public String logout() {
        return "Logout request received.";
    }

    // View a customer profile
    @GetMapping("/{id}")
    public ResponseEntity<String> viewProfile(
            @PathVariable int id) {

        User user = service.getUserById(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found.");
        }

        if (!(user instanceof Customer)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("This endpoint is for customer profiles.");
        }

        Customer customer = (Customer) user;

        String profile = "ID: " + customer.getUserId()
                + "\nName: " + customer.getName()
                + "\nEmail: " + customer.getEmail()
                + "\nAddress: " + customer.getAddress()
                + "\nPhone: " + customer.getPhone();

        return ResponseEntity.ok(profile);
    }

    // Update a customer profile
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProfile(
            @PathVariable int id,
            @RequestBody Customer customer) {

        try {
            customer.setUserId(id);
            service.updateProfile(customer);

            return ResponseEntity.ok("Profile updated successfully.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // Delete a user account
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(
            @PathVariable int id) {

        try {
            service.deleteUser(id);
            return ResponseEntity.ok("Account deleted successfully.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // List customer details without exposing passwords
    @GetMapping
    public List<String> listCustomers() {

        return service.getAllCustomers().stream()
                .map(customer ->
                        "ID: " + customer.getUserId()
                                + ", Name: " + customer.getName()
                                + ", Email: " + customer.getEmail())
                .collect(Collectors.toList());
    }

    // Request body for login
    public static class LoginRequest {

        private String email;
        private String password;

        public LoginRequest() {
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

