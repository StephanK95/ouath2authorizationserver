package com.klamann.ouath2authorizationserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class RestAuthController {

    private AuthService authService;

    private AuthRequest authRequest;

    @Autowired
    public RestAuthController(AuthService authService, AuthRequest authRequest) {
        this.authService = authService;
        this.authRequest = authRequest;
    }


    @PostMapping(
            path = "/token",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public TokenResponse postToken(HttpServletRequest request, @RequestParam MultiValueMap<String,String> requestMap) throws IOException {

        if (!(requestMap.containsKey("code") || requestMap.containsKey("refresh_token")) ||
                !requestMap.containsKey("grant_type")
        ) {
            throw new IllegalStateException("POST Parameter an den token Endpunkt stimmen nicht");
        }

        ClientInformation clientInformation = new ClientInformation();
        if (requestMap.containsKey("code")) {
            clientInformation.setCode(requestMap.get("code").get(0));
        } else if (requestMap.containsKey("refresh_token")) {
            clientInformation.setRefreshToken(requestMap.get("refresh_token").get(0));
        }

        String grant_type = requestMap.get("grant_type").get(0);
        if (grant_type.equalsIgnoreCase(GrantType.AUTHORIATIONCODE.getGrantTypeNames())) {
            clientInformation.setGrantType(GrantType.AUTHORIATIONCODE);
        } else if (grant_type.equalsIgnoreCase(GrantType.REFRESHTOKEN.getGrantTypeNames())) {
            clientInformation.setGrantType(GrantType.REFRESHTOKEN);
        }

        return authService.verifyGetTokenRequest(request, clientInformation);

    }

}
