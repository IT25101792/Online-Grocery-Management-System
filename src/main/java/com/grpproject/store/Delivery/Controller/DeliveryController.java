package com.grpproject.store.Delivery.Controller;

import java.util.List;
import java.util.ArrayList;

import com.grpproject.store.Delivery.Model.Delivery;
import com.grpproject.store.Delivery.Service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping({"/admin/deliveries", "/admin/delivery"})
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // 1. READ ALL
    @GetMapping
    public String showAdminDeliveryPage(Model model) {
        List<Delivery> deliveries = deliveryService.getAllDeliveries();
        model.addAttribute("deliveries", deliveries != null ? deliveries : new ArrayList<>());

        if (!model.containsAttribute("newDelivery")) {
            model.addAttribute("newDelivery", new Delivery());
        }
        return "admin-delivery";
    }

    // 2. CREATE
    @PostMapping("/add")
    public String createDelivery(
            @ModelAttribute("newDelivery") Delivery delivery,
            RedirectAttributes redirectAttributes) {

        if (delivery.getDeliveryID() == null || delivery.getDeliveryID().trim().isEmpty()) {
            delivery.setDeliveryID(delivery.getOrderID());
        }

        boolean created = deliveryService.createDelivery(delivery);

        if (created) {
            redirectAttributes.addFlashAttribute("successMessage", "New delivery record created successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create delivery. Delivery ID may already exist or is invalid.");
        }

        return "redirect:/admin/deliveries";
    }

    // 3. UPDATE STATUS
    @PostMapping("/update-status")
    public String updateDeliveryStatus(
            @RequestParam(value = "deliveryID", required = false) String deliveryID,
            @RequestParam(value = "deliveryStatus", required = false) String deliveryStatus,
            RedirectAttributes redirectAttributes) {

        if (deliveryID == null || deliveryStatus == null || deliveryID.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid delivery ID or status provided.");
            return "redirect:/admin/deliveries";
        }

        boolean updated = deliveryService.updateDeliveryStatus(deliveryID, deliveryStatus);

        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "Delivery status updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update delivery status.");
        }

        return "redirect:/admin/deliveries";
    }

    // 4. DELETE (Handles both URL path variable and POST parameter)
    @RequestMapping(value = {"/delete/{id}", "/delete"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String deleteDelivery(
            @PathVariable(value = "id", required = false) String pathId,
            @RequestParam(value = "deliveryID", required = false) String paramId,
            RedirectAttributes redirectAttributes) {

        String targetId = (pathId != null && !pathId.trim().isEmpty()) ? pathId : paramId;

        if (targetId == null || targetId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid delivery ID provided for deletion.");
            return "redirect:/admin/deliveries";
        }

        boolean deleted = deliveryService.deleteDelivery(targetId);

        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Delivery record deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete delivery record.");
        }

        return "redirect:/admin/deliveries";
    }
}