package com.tool.hub.serviceImpl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.tool.hub.dto.PaymentVerifyDto;
import com.tool.hub.dto.SubscriptionRequestDto;
import com.tool.hub.service.PaymentService;
import com.tool.hub.service.SubscriptionService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final SubscriptionService subscriptionService;

    public PaymentServiceImpl(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Override
    public String createOrder(SubscriptionRequestDto dto) throws Exception {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        int amount = 0;

        if ("SINGLE_TOOL".equals(dto.getPlanType())) {
            if (dto.getDurationMonths() == 1) {
                amount = 4900;
            } else if (dto.getDurationMonths() == 6) {
                amount = 19900;
            } else {
                amount = 29900;
            }
        }

        if ("CATEGORY_VIP".equals(dto.getPlanType())) {
            if (dto.getDurationMonths() == 1) {
                amount = 14900;
            } else if (dto.getDurationMonths() == 6) {
                amount = 59900;
            } else {
                amount = 99900;
            }
        }

        if ("TOOLHUB_PRO".equals(dto.getPlanType())) {
            if (dto.getDurationMonths() == 1) {
                amount = 29900;
            } else if (dto.getDurationMonths() == 6) {
                amount = 129900;
            } else {
                amount = 199900;
            }
        }
        JSONObject options = new JSONObject();

        options.put("amount", amount);

        options.put("currency", "INR");

        options.put("receipt", "toolhub_" + System.currentTimeMillis());

        Order order = client.orders.create(options);

        return order.toString();
    }

    @Override
    public String verifyPayment(String email, PaymentVerifyDto dto)
        throws Exception {
        String payload =
            dto.getRazorpayOrderId() + "|" + dto.getRazorpayPaymentId();

        boolean valid = Utils.verifySignature(
            payload,
            dto.getRazorpaySignature(),
            keySecret
        );

        if (!valid) {
            throw new RuntimeException("Invalid Payment Signature");
        }

        subscriptionService.createSubscription(email, dto.getSubscription());

        return "Payment Verified & Subscription Activated";
    }
}
