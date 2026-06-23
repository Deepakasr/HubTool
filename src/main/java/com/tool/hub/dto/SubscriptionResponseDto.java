package com.tool.hub.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscriptionResponseDto {

    private String planType;

    private String category;

    private String toolName;

    private LocalDate expiryDate;

    private boolean active;
    private LocalDate startDate;
    private long daysLeft;
}
