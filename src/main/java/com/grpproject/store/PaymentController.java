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

    // Display checkout page
    @GetMapping({"/", "/index", "/checkout"})
    public String showCheckoutPage(Model model) {

        model.addAttribute("paymentRecords",
                paymentRepository.findAll());

        model.addAttribute("payment", new Payment());

        return "index";
    }

    // Save payment
    @PostMapping("/payments/save")
    public String savePaymentRecord(
            @ModelAttribute Payment payment,
            @RequestParam("paymentType") String type) {

        Payment newPayment;

        // Create the relevant child object based on inheritance
        switch (type) {
            case "CASH_ON_DELIVERY":
                newPayment = new CashOnDelivery();
                break;
            case "CARD":
                newPayment = new CardPayment();
                break;
            case "ONLINE_WALLET":
                newPayment = new OnlineWallet();
                break;
            case "BANK_TRANSFER":
                newPayment = new BankTransfer();
                break;
            default:
                throw new IllegalArgumentException("Invalid payment type");
        }

        // Copy common details from input form data
        newPayment.setPaymentId(payment.getPaymentId());
        newPayment.setOrderId(payment.getOrderId());
        newPayment.setCustomerName(payment.getCustomerName());
        newPayment.setPhoneNumber(payment.getPhoneNumber());
        newPayment.setItems(payment.getItems());
        newPayment.setAmount(payment.getAmount());
        newPayment.setPaymentDate(payment.getPaymentDate());

        paymentRepository.save(newPayment);

        return "redirect:/";
    }

    // Delete payment
    @GetMapping("/payments/delete")
    public String deletePaymentRecord(
            @RequestParam("id") String paymentId) {

        paymentRepository.deleteById(paymentId);

        return "redirect:/";
    }
}