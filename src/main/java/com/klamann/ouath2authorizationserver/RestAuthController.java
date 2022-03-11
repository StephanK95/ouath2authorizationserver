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

        if (!requestMap.containsKey("code") ||
                !requestMap.containsKey("grant_type")
        ) {
            throw new IllegalStateException("POST Parameter an den token Endpunkt stimmen nicht");
        }

        ClientInformation clientInformation = new ClientInformation();
        clientInformation.setCode(requestMap.get("code").get(0));
        if (requestMap.get("grant_type").get(0).equals("authorization_code")) {
            clientInformation.setGrantType(GrantType.AUTHORIATIONCODE);
        }

        return authService.verifyGetTokenRequest(request, clientInformation);

    }

}
