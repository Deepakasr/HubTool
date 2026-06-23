package com.tool.hub.serviceImpl;

import com.tool.hub.dto.ProfileResponseDto;
import com.tool.hub.entity.User;
import com.tool.hub.repository.SubscriptionRepo;
import com.tool.hub.repository.UserRepo;
import com.tool.hub.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final SubscriptionRepo subscriptionRepo;

    public UserServiceImpl(
        UserRepo userRepo,
        SubscriptionRepo subscriptionRepo
    ) {
        this.userRepo = userRepo;
        this.subscriptionRepo = subscriptionRepo;
    }

    @Override
    public ProfileResponseDto getProfile(String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        boolean vip = subscriptionRepo.existsByUserIdAndActiveTrue(
            user.getId()
        );
        return new ProfileResponseDto(
            user.getName(),
            user.getEmail(),
            user.getRole(),
            vip
        );
    }
}
