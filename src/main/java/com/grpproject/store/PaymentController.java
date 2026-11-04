package com.grpproject.store;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentFileRepository repository;

    public PaymentController(PaymentFileRepository repository) {
        this.repository = repository;
    }

    // Endpoint to Add a Payment (POST)
    @PostMapping
    public ResponseEntity<String> createPayment(@RequestBody Payment payment) {
        repository.save(payment);
        return ResponseEntity.ok("Payment saved successfully to text file!");
    }

    // Endpoint to View All Payments (GET)
    @GetMapping
    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

    // Endpoint to Delete a Payment (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePayment(@PathVariable String id) {
        boolean success = repository.deleteById(id);
        return success ? ResponseEntity.ok("Payment deleted successfully.")
                : ResponseEntity.notFound().build();
    }

    // Endpoint to Update a Payment (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePayment(@PathVariable String id, @RequestBody Payment updated) {
        boolean success = repository.update(id, updated);
        return success ? ResponseEntity.ok("Payment updated successfully in text file.")
                : ResponseEntity.notFound().build();
    }
}