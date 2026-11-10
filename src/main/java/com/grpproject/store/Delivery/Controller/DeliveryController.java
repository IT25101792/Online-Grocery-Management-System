package com.grpproject.store.Delivery.Controller;

import com.grpproject.store.Delivery.Model.Delivery;
import com.grpproject.store.Delivery.Model.DeliveryStatus;
import com.grpproject.store.Delivery.Service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping({"/admin/deliveries", "/admin/delivery"})
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // 1. READ ALL (Matches diagram listDeliveries)
    @GetMapping
    public String listDeliveries(Model model) {
        List<Delivery> deliveries = deliveryService.getAllDeliveries();
        model.addAttribute("deliveries", deliveries);
        return "admin-delivery";
    }

    // 2. CREATE (Matches diagram createDelivery)
    @PostMapping("/add")
    public String createDelivery(
            @RequestParam("orderId") int orderId,
            @RequestParam("address") String address,
            RedirectAttributes redirectAttributes) {

        try {
            deliveryService.createDelivery(orderId, address);
            redirectAttributes.addFlashAttribute("successMessage", "New delivery record created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create delivery.");
        }
        return "redirect:/admin/deliveries";
    }

    // 3. ASSIGN DRIVER (Matches diagram assignDriver)
    @PostMapping("/assign-driver")
    public String assignDriver(
            @RequestParam("deliveryId") int deliveryId,
            @RequestParam("driver") String driver,
            RedirectAttributes redirectAttributes) {

        deliveryService.assignDriver(deliveryId, driver);
        redirectAttributes.addFlashAttribute("successMessage", "Driver assigned successfully!");
        return "redirect:/admin/deliveries";
    }

    // 4. UPDATE STATUS (Matches diagram updateStatus)
    @PostMapping("/update-status")
    public String updateStatus(
            @RequestParam("deliveryId") int deliveryId,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {

        try {
            DeliveryStatus deliveryStatus = DeliveryStatus.valueOf(status.toUpperCase());
            deliveryService.updateStatus(deliveryId, deliveryStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Delivery status updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid status value provided.");
        }
        return "redirect:/admin/deliveries";
    }

    // 5. DELETE (Matches diagram deleteDelivery)
    @PostMapping("/delete/{id}")
    public String deleteDelivery(
            @PathVariable("id") int deliveryId,
            RedirectAttributes redirectAttributes) {

        deliveryService.deleteDelivery(deliveryId);
        redirectAttributes.addFlashAttribute("successMessage", "Delivery record deleted successfully!");
        return "redirect:/admin/deliveries";
    }
}