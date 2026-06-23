package com.tool.hub.service;

import com.tool.hub.dto.LoginDto;
import com.tool.hub.dto.LoginResponseDto;
import com.tool.hub.dto.UserDto;
import com.tool.hub.dto.UserProfileDto;
import com.tool.hub.dto.VerifyOtpDto;

public interface AuthService {
    String register(UserDto dto);

    String verifyOtp(VerifyOtpDto dto);

    LoginResponseDto login(LoginDto dto);

    UserProfileDto getCurrentUser(String email);
}
