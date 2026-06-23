package com.tool.hub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileResponseDto {

    private String name;
    private String email;
    private String role;
    private boolean vip;
}
