package com.tool.hub.serviceImpl;

import com.tool.hub.dto.AccessResponseDto;
import com.tool.hub.dto.SubscriptionRequestDto;
import com.tool.hub.dto.SubscriptionResponseDto;
import com.tool.hub.entity.Subscription;
import com.tool.hub.entity.User;
import com.tool.hub.repository.SubscriptionRepo;
import com.tool.hub.repository.UserRepo;
import com.tool.hub.service.SubscriptionService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepo subscriptionRepo;
    private final UserRepo userRepo;

    public SubscriptionServiceImpl(
        SubscriptionRepo subscriptionRepo,
        UserRepo userRepo
    ) {
        this.subscriptionRepo = subscriptionRepo;
        this.userRepo = userRepo;
    }

    @Override
    public String createSubscription(String email, SubscriptionRequestDto dto) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if ("SINGLE_TOOL".equals(dto.getPlanType())) {
            boolean exists =
                subscriptionRepo.existsByUserIdAndPlanTypeAndToolNameAndActiveTrue(
                    user.getId(),
                    dto.getPlanType(),
                    dto.getToolName()
                );

            if (exists) {
                return "Already Subscribed";
            }
        }

        if (
            dto.getDurationMonths() != 1 &&
            dto.getDurationMonths() != 6 &&
            dto.getDurationMonths() != 12
        ) {
            throw new RuntimeException("Invalid Duration");
        }

        Subscription subscription = new Subscription();

        subscription.setUserId(user.getId());

        subscription.setPlanType(dto.getPlanType());

        subscription.setCategory(dto.getCategory());

        subscription.setToolName(dto.getToolName());

        subscription.setStartDate(LocalDate.now());

        subscription.setExpiryDate(
            LocalDate.now().plusMonths(dto.getDurationMonths())
        );

        subscription.setActive(true);

        subscriptionRepo.save(subscription);

        return "Subscription Activated";
    }

    @Override
    public AccessResponseDto checkAccess(
        String email,
        String category,
        String toolName
    ) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Subscription> subscriptions =
            subscriptionRepo.findByUserIdAndActiveTrue(user.getId());

        for (Subscription sub : subscriptions) {
            if ("TOOLHUB_PRO".equals(sub.getPlanType())) {
                return new AccessResponseDto(true);
            }

            if (
                "CATEGORY_VIP".equals(sub.getPlanType()) &&
                category.equals(sub.getCategory())
            ) {
                return new AccessResponseDto(true);
            }

            if (
                "SINGLE_TOOL".equals(sub.getPlanType()) &&
                toolName.equals(sub.getToolName())
            ) {
                return new AccessResponseDto(true);
            }
        }

        return new AccessResponseDto(false);
    }

    @Override
    public List<SubscriptionResponseDto> getMySubscriptions(String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepo
            .findByUserIdAndActiveTrue(user.getId())
            .stream()
            .map(sub ->
                new SubscriptionResponseDto(
                    sub.getPlanType(),
                    sub.getCategory(),
                    sub.getToolName(),
                    sub.getExpiryDate(),
                    sub.isActive(),
                    sub.getStartDate(),
                    ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        sub.getExpiryDate()
                    )
                )
            )
            .collect(Collectors.toList());
    }
}
