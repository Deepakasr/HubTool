package com.tool.hub.service;

import com.tool.hub.dto.PaymentVerifyDto;
import com.tool.hub.dto.SubscriptionRequestDto;

public interface PaymentService {
    String createOrder(SubscriptionRequestDto dto) throws Exception;

    String verifyPayment(String email, PaymentVerifyDto dto) throws Exception;
}
