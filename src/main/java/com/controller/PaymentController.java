package com.controller;

import com.entity.Payment;
import com.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("payments", paymentService.getAllPayments());
        model.addAttribute("payment", new Payment());
        return "index";
    }

    @GetMapping("/payment")
    public String getPaymentById(@RequestParam Long id, Model model) {
        model.addAttribute("payment", paymentService.getPaymentById(id));
        return "payment";
    }

    @PostMapping("/payments")
    public String createPayment(@ModelAttribute Payment payment) {
        paymentService.createPayment(payment);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return "redirect:/";
    }
}
