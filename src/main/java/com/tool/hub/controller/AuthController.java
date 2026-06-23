package com.tool.hub.controller;

import com.tool.hub.dto.LoginDto;
import com.tool.hub.dto.LoginResponseDto;
import com.tool.hub.dto.ProfileResponseDto;
import com.tool.hub.dto.UserDto;
import com.tool.hub.dto.UserProfileDto;
import com.tool.hub.dto.VerifyOtpDto;
import com.tool.hub.service.AuthService;
import com.tool.hub.service.UserService;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserDto dto) {
        return authService.register(dto);
    }

    @PostMapping("/verify")
    public String verifyOtp(@RequestBody VerifyOtpDto dto) {
        return authService.verifyOtp(dto);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginDto dto) {
        return authService.login(dto);
    }

    @GetMapping("/me")
    public UserProfileDto me(Principal principal) {
        return authService.getCurrentUser(principal.getName());
    }

    @GetMapping("/profile")
    public ProfileResponseDto getProfile(Principal principal) {
        return userService.getProfile(principal.getName());
    }
}
