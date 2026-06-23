package com.tool.hub.dto;

public class AccessResponseDto {

    private boolean access;

    public AccessResponseDto(boolean access) {
        this.access = access;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }
}
