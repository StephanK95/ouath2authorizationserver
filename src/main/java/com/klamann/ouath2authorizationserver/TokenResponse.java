package com.klamann.ouath2authorizationserver;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {

    private String access_token;
    private String clientId;
    private String scope;
}
