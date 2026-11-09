package com.grpproject.store.Delivery.Controller;

import com.grpproject.store.Delivery.Model.Delivery;
import com.grpproject.store.Delivery.Service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // Displays the Admin dashboard with all deliveries and update form
    @GetMapping
    public String showAdminDeliveryPage(Model model) {
        model.addAttribute("deliveries", deliveryService.getAllDeliveries());
        return "admin-delivery"; // Maps to admin-delivery.html in templates
    }

    // Handles status update form submission
    // Handles status update form submission
    @PostMapping("/update-status")
    public String updateDeliveryStatus(
            @RequestParam("deliveryID") String deliveryID,
            @RequestParam("deliveryStatus") String deliveryStatus,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        boolean updated = deliveryService.updateDeliveryStatus(
                deliveryID, deliveryStatus);

        if (updated) {
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Delivery status updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Failed to update delivery status.");
        }

        return "redirect:/admin/deliveries";
    }
}
