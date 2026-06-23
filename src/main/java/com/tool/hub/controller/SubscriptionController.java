package com.tool.hub.controller;

import com.tool.hub.dto.AccessResponseDto;
import com.tool.hub.dto.SubscriptionRequestDto;
import com.tool.hub.dto.SubscriptionResponseDto;
import com.tool.hub.service.SubscriptionService;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscription")
@CrossOrigin("*")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/create")
    public String createSubscription(
        @RequestBody SubscriptionRequestDto dto,
        Principal principal
    ) {
        return subscriptionService.createSubscription(principal.getName(), dto);
    }

    @GetMapping("/check")
    public AccessResponseDto checkAccess(
        @RequestParam String category,
        @RequestParam String toolName,
        Principal principal
    ) {
        return subscriptionService.checkAccess(
            principal.getName(),
            category,
            toolName
        );
    }

    @GetMapping("/my-subscriptions")
    public List<SubscriptionResponseDto> getMySubscriptions(
        Principal principal
    ) {
        return subscriptionService.getMySubscriptions(principal.getName());
    }
}
