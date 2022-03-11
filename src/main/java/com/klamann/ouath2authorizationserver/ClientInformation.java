package com.klamann.ouath2authorizationserver;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientInformation {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;
    private GrantType grantType;
    private String code;

    public ClientInformation(String clientId, String clientSecret, String redirectUri, String scope) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.scope = scope;
    }

    public ClientInformation() {
    }
}
