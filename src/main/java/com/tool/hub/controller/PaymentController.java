package com.tool.hub.controller;

import com.tool.hub.dto.PaymentVerifyDto;
import com.tool.hub.dto.SubscriptionRequestDto;
import com.tool.hub.service.PaymentService;
import java.security.Principal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public String createOrder(@RequestBody SubscriptionRequestDto dto)
        throws Exception {
        return paymentService.createOrder(dto);
    }

    @PostMapping("/verify")
    public String verifyPayment(
        @RequestBody PaymentVerifyDto dto,
        Principal principal
    ) throws Exception {
        return paymentService.verifyPayment(principal.getName(), dto);
    }
}
