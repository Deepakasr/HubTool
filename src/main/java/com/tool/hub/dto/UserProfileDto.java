package com.tool.hub.dto;

public class UserProfileDto {

    private Long id;
    private String name;
    private String email;
    private String role;
    private boolean verified;

    public UserProfileDto() {}

    public UserProfileDto(
        Long id,
        String name,
        String email,
        String role,
        boolean verified
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.verified = verified;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isVerified() {
        return verified;
    }
}
