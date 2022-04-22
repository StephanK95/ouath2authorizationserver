package com.klamann.ouath2authorizationserver;

public enum GrantType {

    AUTHORIATIONCODE("authorization_code"),
    REFRESHTOKEN("refresh_token");

    private final String grantTypeNames;

    GrantType(String grantTypeNames) {
        this.grantTypeNames = grantTypeNames;
    }

    public String getGrantTypeNames() {
        return grantTypeNames;
    }
}
