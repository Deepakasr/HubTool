package com.tool.hub.repository;

import com.tool.hub.entity.Subscription;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepo extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserIdAndActiveTrue(Long userId);

    boolean existsByUserIdAndPlanTypeAndToolNameAndActiveTrue(
        Long userId,
        String planType,
        String toolName
    );

    boolean existsByUserIdAndActiveTrue(Long userId);
}
