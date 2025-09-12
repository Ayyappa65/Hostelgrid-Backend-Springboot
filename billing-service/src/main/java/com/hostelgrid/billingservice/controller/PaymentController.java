package com.hostelgrid.billingservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/billing")
public class PaymentController {

    @GetMapping("/payment-status")
    public String getPaymentStatus() {
        return "Payment Service is up and running!";
    }
}
