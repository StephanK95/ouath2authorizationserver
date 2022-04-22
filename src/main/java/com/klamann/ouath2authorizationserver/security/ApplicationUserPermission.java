package com.klamann.ouath2authorizationserver.security;

public enum ApplicationUserPermission {
    OAUTH_LOGIN("oauth:login");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
