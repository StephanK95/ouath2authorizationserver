package com.klamann.ouath2authorizationserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/api/v1")
@Slf4j
public class AuthController {

    private AuthService authService;

    private AuthRequest authRequest;

    @Autowired
    public AuthController(AuthService authService, AuthRequest authRequest) {
        this.authService = authService;
        this.authRequest = authRequest;
    }

    @GetMapping
    public String home() {
        return "home";
    }

    @GetMapping(path = "/authorize")
    public String getAuthorize(
            @RequestParam(value = "clientId", required = false) String clientId,
            @RequestParam(value = "client_id", required = false) String clientIdSynonym,
            @RequestParam("scope") String scope,
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            @RequestParam(value = "redirect_uri", required = false) String redirectUriSynonym,
            @RequestParam("state") String state,
            @RequestParam(value = "responseType", required = false) String responseType,
            @RequestParam(value = "response_type", required = false) String responseTypeSynonym,
            Model model) {
        if (clientId != null) {
            authRequest.setClientId(clientId);
        } else {
            authRequest.setClientId(clientIdSynonym);
        }
        authRequest.setScope(scope);
        if (redirectUri != null) {
            authRequest.setRedirectUri(redirectUri);
        } else {
            authRequest.setRedirectUri(redirectUriSynonym);
        }
        authRequest.setState(state);
        if (responseType != null) {
            authRequest.setResponseType(responseType);
        } else {
            authRequest.setResponseType(responseTypeSynonym);
        }

        String reqId = authService.verifyGetRequest(authRequest);

        authRequest.setReqId(reqId);

        log.info(authRequest.getClientId());
        model.addAttribute("authRequest", authRequest);
        return "authView";
    }

    @PostMapping(
            path = "/approve",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ModelAndView postAutorize(@RequestParam MultiValueMap<String,String> paramMap, HttpServletResponse response) {
        String redirectUri = authService.verifyPostRequest(paramMap);
        return new ModelAndView("redirect:" + redirectUri);
    }


}
