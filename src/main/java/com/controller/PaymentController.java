package com.controller;

import com.entity.Currency;
import com.entity.Payment;
import com.entity.PaymentPriority;
import com.entity.PaymentType;
import com.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Currency currency,
            Model model) {

        Pageable pageable = PageRequest.of(
                0,
                100,
                Sort.by(Sort.Direction.ASC, "id")
        );

        model.addAttribute(
                "payments",
                paymentService.searchPayments(
                        status,
                        currency,
                        null,
                        pageable
                )
        );

        model.addAttribute("payment", new Payment());
        model.addAttribute("currencies", Currency.values());
        model.addAttribute("paymentTypes", PaymentType.values());
        model.addAttribute("priorities", PaymentPriority.values());

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

    @PostMapping("/execute/{id}")
    public String executePayment(@PathVariable Long id) {
        paymentService.executePayment(id);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return "redirect:/";
    }
}
