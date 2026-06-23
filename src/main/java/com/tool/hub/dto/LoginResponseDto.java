package com.tool.hub.dto;

public class LoginResponseDto {

    private String token;
    private String name;
    private String email;
    private boolean vip;

    public LoginResponseDto() {}

    public LoginResponseDto(
        String token,
        String name,
        String email,
        boolean vip
    ) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.vip = vip;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
