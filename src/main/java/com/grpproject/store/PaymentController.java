package com.grpproject.store;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentController {

    private final PaymentFileRepository paymentRepository;

    public PaymentController(PaymentFileRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping({"/", "/index", "/checkout"})
    public String showCheckoutPage(Model model) {
        model.addAttribute("paymentRecords", paymentRepository.findAll());
        return "index";
    }

    @PostMapping("/payments/save")
    public String savePaymentRecord(@ModelAttribute Payment payment) {
        paymentRepository.save(payment);
        return "redirect:/";
    }

    @GetMapping("/payments/delete")
    public String deletePaymentRecord(@RequestParam("id") String paymentId) {
        paymentRepository.deleteById(paymentId);
        return "redirect:/";
    }
}
