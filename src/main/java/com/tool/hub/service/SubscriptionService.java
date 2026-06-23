package com.tool.hub.service;

import com.tool.hub.dto.AccessResponseDto;
import com.tool.hub.dto.SubscriptionRequestDto;
import com.tool.hub.dto.SubscriptionResponseDto;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public interface SubscriptionService {
    String createSubscription(String email, SubscriptionRequestDto dto);

    AccessResponseDto checkAccess(
        String email,
        String category,
        String toolName
    );

    List<SubscriptionResponseDto> getMySubscriptions(String email);
}
