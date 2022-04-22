package com.klamann.ouath2authorizationserver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.Serializable;
import java.util.List;

@Component
@Data
@RequestScope
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenEntry implements Serializable {

    private String accessToken;
    private String clientId;
    private String refreshToken;
    private List<String> scope;

}
