package com.tool.hub.serviceImpl;

import com.tool.hub.dto.LoginDto;
import com.tool.hub.dto.LoginResponseDto;
import com.tool.hub.dto.UserDto;
import com.tool.hub.dto.UserProfileDto;
import com.tool.hub.dto.VerifyOtpDto;
import com.tool.hub.entity.EmailOtp;
import com.tool.hub.entity.User;
import com.tool.hub.repository.EmailOtpRepo;
import com.tool.hub.repository.SubscriptionRepo;
import com.tool.hub.repository.UserRepo;
import com.tool.hub.security.JwtUtil;
import com.tool.hub.service.AuthService;
import com.tool.hub.service.EmailService;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final UserRepo userRepo;
    private final EmailOtpRepo emailOtpRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
        UserRepo userRepo,
        EmailOtpRepo emailOtpRepo,
        SubscriptionRepo subscriptionRepo,
        PasswordEncoder passwordEncoder,
        EmailService emailService,
        JwtUtil jwtUtil
    ) {
        this.userRepo = userRepo;
        this.emailOtpRepo = emailOtpRepo;
        this.subscriptionRepo = subscriptionRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String register(UserDto dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setVerified(false);
        user.setRole("USER");

        userRepo.save(user);
        String otp = String.valueOf((int) (Math.random() * 900000 + 100000));
        EmailOtp emailOtp = new EmailOtp();

        emailOtp.setEmail(dto.getEmail());

        emailOtp.setOtp(otp);

        emailOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        emailService.sendOtp(dto.getEmail(), otp);

        emailOtpRepo.deleteByEmail(dto.getEmail());

        emailOtpRepo.save(emailOtp);

        return "OTP Sent Successfully";
    }

    @Override
    public String verifyOtp(VerifyOtpDto dto) {
        EmailOtp emailOtp = emailOtpRepo
            .findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("OTP Not Found"));

        if (!emailOtp.getOtp().equals(dto.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if (emailOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP Expired");
        }

        User user = userRepo
            .findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("User Not Found"));

        user.setVerified(true);

        userRepo.save(user);

        emailOtpRepo.delete(emailOtp);

        return "Email Verified Successfully";
    }

    @Override
    public LoginResponseDto login(LoginDto dto) {
        User user = userRepo
            .findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your email first");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        boolean vip = subscriptionRepo.existsByUserIdAndActiveTrue(
            user.getId()
        );

        return new LoginResponseDto(
            token,
            user.getName(),
            user.getEmail(),
            vip
        );
    }

    @Override
    public UserProfileDto getCurrentUser(String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserProfileDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.isVerified()
        );
    }
}
