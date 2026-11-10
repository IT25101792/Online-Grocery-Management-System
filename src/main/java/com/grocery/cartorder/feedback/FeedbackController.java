package com.grocery.cartorder.feedback;

import com.grocery.cartorder.service.OrderService;
import com.grocery.cartorder.service.ProductStockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/** Web controller for the Feedback & Reviews page. */
@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService service;
    private final OrderService orderService;
    private final ProductStockService productStockService;

    public FeedbackController(FeedbackService service,
                              OrderService orderService,
                              ProductStockService productStockService) {
        this.service = service;
        this.orderService = orderService;
        this.productStockService = productStockService;
    }

    @GetMapping
    public String showReviewForm(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String productId,
            Model model) {
        loadPage(customerId, productId, model);
        return "feedback";
    }

    @GetMapping("/reviews")
    public String viewReviewsByProduct(@RequestParam String productId, Model model) {
        loadPage(null, productId, model);
        return "feedback";
    }

    @GetMapping("/mine")
    public String listMyFeedback(@RequestParam String customerId, Model model) {
        loadPage(customerId, null, model);
        return "feedback";
    }

    @GetMapping("/admin")
    public String listAllFeedback(Model model) {
        loadPage(null, null, model);
        return "feedback";
    }

    @PostMapping("/submit-review")
    public String submitReview(@RequestParam String customerId,
                               @RequestParam String productId,
                               @RequestParam int rating,
                               @RequestParam(required = false) String title,
                               @RequestParam String comment,
                               RedirectAttributes redirectAttributes) {
        try {
            service.submitProductReview(customerId, productId, rating, title, comment);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Your product review was submitted and is waiting for admin approval.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        redirectAttributes.addAttribute("customerId", customerId);
        redirectAttributes.addAttribute("productId", productId);
        return "redirect:/feedback";
    }

    @PostMapping("/submit-service-feedback")
    public String submitServiceFeedback(@RequestParam String customerId,
                                        @RequestParam String orderId,
                                        @RequestParam(required = false) String category,
                                        @RequestParam String comment,
                                        RedirectAttributes redirectAttributes) {
        try {
            service.submitServiceFeedback(customerId, orderId, category, comment);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Your service feedback was submitted and is waiting for admin approval.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        redirectAttributes.addAttribute("customerId", customerId);
        return "redirect:/feedback";
    }

    @PostMapping("/edit/{id}")
    public String editFeedback(@PathVariable int id,
                               @RequestParam String customerId,
                               @RequestParam String comment,
                               @RequestParam(defaultValue = "0") int rating,
                               RedirectAttributes redirectAttributes) {
        try {
            Feedback existing = service.getFeedbackById(id);
            checkCustomerOwnsFeedback(existing, customerId);
            service.updateFeedback(id, comment, rating);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Feedback updated. It is pending approval again.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        redirectAttributes.addAttribute("customerId", customerId);
        return "redirect:/feedback";
    }

    @PostMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable int id,
                                 @RequestParam String customerId,
                                 RedirectAttributes redirectAttributes) {
        try {
            checkCustomerOwnsFeedback(service.getFeedbackById(id), customerId);
            service.deleteFeedback(id);
            redirectAttributes.addFlashAttribute("successMessage", "Feedback deleted.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        redirectAttributes.addAttribute("customerId", customerId);
        return "redirect:/feedback";
    }

    @PostMapping("/moderate/{id}")
    public String moderateFeedback(@PathVariable int id,
                                   @RequestParam String status,
                                   RedirectAttributes redirectAttributes) {
        try {
            service.moderate(id, FeedbackStatus.valueOf(status.toUpperCase()));
            redirectAttributes.addFlashAttribute("successMessage", "Feedback status updated.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Invalid feedback ID or status. Choose PENDING, APPROVED or REJECTED.");
        }
        return "redirect:/feedback";
    }

    @PostMapping("/reply/{id}")
    public String replyToFeedback(@PathVariable int id,
                                  @RequestParam String reply,
                                  RedirectAttributes redirectAttributes) {
        try {
            service.addAdminReply(id, reply);
            redirectAttributes.addFlashAttribute("successMessage", "Admin reply saved.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/feedback";
    }

    private void loadPage(String customerId, String productId, Model model) {
        model.addAttribute("productIds", productStockService.getAllStock().keySet());
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("allFeedback", service.getAllFeedback());
        model.addAttribute("myFeedback", customerId == null || customerId.isBlank()
                ? List.of() : service.getFeedbackByCustomer(customerId));
        model.addAttribute("visibleReviews", productId == null || productId.isBlank()
                ? List.of() : service.getReviewsByProduct(productId));
        model.addAttribute("selectedCustomerId", customerId == null ? "" : customerId);
        model.addAttribute("selectedProductId", productId == null ? "" : productId);
        model.addAttribute("averageRating", productId == null || productId.isBlank()
                ? 0.0 : service.getAverageRating(productId));
    }

    private void checkCustomerOwnsFeedback(Feedback feedback, String customerId) {
        if (feedback == null || customerId == null
                || !customerId.equals(feedback.getCustomerId())) {
            throw new IllegalArgumentException("Feedback was not found for this customer.");
        }
    }
}
