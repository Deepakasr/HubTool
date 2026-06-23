package com.tool.hub.service;

import com.tool.hub.dto.ProfileResponseDto;

public interface UserService {
    ProfileResponseDto getProfile(String email);
}
