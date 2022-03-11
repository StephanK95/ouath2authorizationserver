package com.klamann.ouath2authorizationserver;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.Serializable;

@Component
@Data
@RequestScope
public class AuthRequest implements Serializable {

    private String clientId;
    private String scope;
    private String redirectUri;
    private String state;
    private String reqId;
    private String responseType;

}
