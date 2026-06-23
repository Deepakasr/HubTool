package com.tool.hub.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String planType;

    private String category;

    private String toolName;

    private LocalDate startDate;

    private LocalDate expiryDate;

    private boolean active;
}
