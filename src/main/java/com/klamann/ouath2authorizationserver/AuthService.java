package com.klamann.ouath2authorizationserver;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AuthService {

    private static final String home = System.getProperty("user.home");

    private static final String TOKENTYPE = "Bearer";

    Map<String, Map<String, String>> requestIds = new HashMap<>();

    Map<String, Map<String, ?>> codesMap = new HashMap<>();

    private List<ClientInformation> clientList = List.of(new ClientInformation("oauth-client-1", "oauth-client-secret-1", "http://localhost:8082/api/v1/callback", "foo bar get"));

    public String verifyGetRequest(AuthRequest authRequest) throws UnsupportedEncodingException {
        if (clientList.stream().filter(client -> client.getClientId().equals(authRequest.getClientId())).collect(Collectors.toList()).size() == 0) {
            throw new IllegalStateException("Client id unbekannt");
        }
        Optional<ClientInformation> clientInformation = clientList.stream().filter(client -> client.getClientId().equals(authRequest.getClientId())).findFirst();
        if (!encodeValue(clientInformation.get().getRedirectUri()).equals(authRequest.getRedirectUri())) {
            throw new IllegalStateException("Redirect url matchen nicht");
        }

        checkScope(authRequest.getScope(), clientInformation.get());

        String reqId = RandomStringUtils.random(8, true, false);

        requestIds.put(reqId, Map.of("clientId", authRequest.getClientId(), "redirectUri", authRequest.getRedirectUri(), "state", authRequest.getState(), "scope", authRequest.getScope()));

        return reqId;

    }

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    private String decodeValue(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
    }

    private void checkScope(String scope, ClientInformation clientInformation) {
        for (String singleScope: clientInformation.getScope().split(" ")) {
            if (!Arrays.stream(scope.split(" ")).collect(Collectors.toList()).contains(scope)) {
                throw new IllegalStateException("Scope für diese ClientId nicht verfügbar");
            } else {
                return;
            }
        }
    }

    public String verifyPostRequest(MultiValueMap<String,String> requestMap) throws UnsupportedEncodingException {

        if (!requestMap.containsKey("clientId") ||
                !requestMap.containsKey("scope") ||
                !requestMap.containsKey("reqId") ||
                !requestMap.containsKey("redirectUri") ||
                !requestMap.containsKey("state") ||
                !requestMap.containsKey("responseType") ||
                !requestMap.containsKey("user")
        ) {
            throw new IllegalStateException("POST Parameter an den approve Endpunkt stimmen nicht");
        }

        String clientId = requestMap.get("clientId").get(0);
        String scope = requestMap.get("scope").get(0);
        String reqId = requestMap.get("reqId").get(0);
        String redirect_uri = requestMap.get("redirectUri").get(0);
        String state = requestMap.get("state").get(0);
        String responseType = requestMap.get("responseType").get(0);
        String user = requestMap.get("user").get(0);

        Map<String,String> queryParams = new HashMap<>();
        try {
            queryParams = requestIds.get(reqId);
        } catch (NullPointerException ex) {
            throw new IllegalStateException("No matching authorization request");
        }

        requestIds.remove(reqId);

        ClientInformation clientInformation = new ClientInformation(clientId, null, redirect_uri, scope);

        if (responseType.equals("code")) {

            String code = RandomStringUtils.random(8, true, false);

            codesMap.put(code, Map.of("requestParams", queryParams, "scope", scope, "user", user));

            checkScope(scope, clientInformation);

            return buildRedirectUri(queryParams.get("redirectUri"), code, queryParams.get("state"));

        }

        return null;
    }

    private String buildRedirectUri(String redirectUri, String code, String state) throws UnsupportedEncodingException {
        return decodeValue(redirectUri) + "?code=" + code  + "&state=" + state;
    }


    public TokenResponse verifyGetTokenRequest(HttpServletRequest request, ClientInformation clientInformation) throws IOException {

        if (
                clientInformation.getCode() == null ||
                        clientInformation.getGrantType() == null) {
            new IllegalStateException("Auth Code oder Grant Type fehlen und müssen gesetzt sein.");
        }

        boolean hasAuthHeader = false;
        Iterator<String> headerIterator = request.getHeaderNames().asIterator();
        while (headerIterator.hasNext()) {
            String headerName = headerIterator.next();
            if (headerName.equalsIgnoreCase("Authorization")) {
                hasAuthHeader = true;
                break;
            }
        }

        if ((clientInformation.getClientId() != null && clientInformation.getClientSecret() != null) && hasAuthHeader) {
            new IllegalStateException("Client attempted to authenticate with multiple methods");
        }

        if (hasAuthHeader) {
            String basicToken = request.getHeader("Authorization").replace("Basic ", "");
            String encodedToken = new String(Base64.getDecoder().decode(basicToken), "UTF-8");
            clientInformation.setClientId(encodedToken.split(":")[0]);
            clientInformation.setClientSecret(encodedToken.split(":")[1]);
        } else {
            if ((clientInformation.getClientId() != null && clientInformation.getClientSecret() != null)) {
                new IllegalStateException("Client did not pass client credentials");
            }
        }

        Optional<ClientInformation> trueClientInformation = clientList.stream().filter(client -> client.getClientId().equals(clientInformation.getClientId())).findFirst();

        trueClientInformation.orElseThrow(() -> new IllegalStateException("Client id unbekannt"));

        if (!clientInformation.getClientSecret().equals(trueClientInformation.get().getClientSecret())) {
            new IllegalStateException("Client Secret falsch");
        }

        if (clientInformation.getGrantType().equals(GrantType.AUTHORIATIONCODE)) {

            if (!codesMap.containsKey(clientInformation.getCode())) {
                new IllegalStateException(String.format("Auth Token %s unbekannt.", clientInformation.getCode()));
            }
            Map<String, ?> authCode = codesMap.get(clientInformation.getCode());

            Map<String, String> requestParams = (Map<String, String>) authCode.get("requestParams");

            if (!requestParams.get("clientId").equals(clientInformation.getClientId())) {
                new IllegalStateException(String.format("Client mismatch, expected %s got %s.", (String) authCode.get("clientId"), (String) clientInformation.getClientId()));
            }

            codesMap.remove(clientInformation.getCode());

            StringBuilder accessTokenScope = new StringBuilder();
            String scopeFromAuthToken = (String) authCode.get("scope");
            Arrays.stream(scopeFromAuthToken.split(" ")).forEach(part -> accessTokenScope.append(part + " "));

            TokenResponse tokenResponse = getTokenResponse(clientInformation, accessTokenScope.toString());

            return tokenResponse;


        } else if (clientInformation.getGrantType().equals(GrantType.REFRESHTOKEN)){

            log.info("Getting new access token for refresh token: ", clientInformation.getRefreshToken());
            List<String> lines = Files.readAllLines(Path.of( home + File.separator + "Documents" + File.separator + "GitHub" + File.separator + "ouath2authorizationserver" + File.separator + "accessTokens.txt"));

            //TODO schauen ob refresh Token expired ist

            List<AccessTokenEntry> accessTokenEntries = lines.stream().map(s -> {
                String[] splittedString = s.split(" ");
                List<String> scopeList = new ArrayList<>();
                for (int i = 3; i < splittedString.length; i++) {
                    scopeList.add(splittedString[i]);
                }
                return new AccessTokenEntry(splittedString[0], splittedString[1], splittedString[2], scopeList);
            }).collect(Collectors.toList());

            Optional<AccessTokenEntry> first = accessTokenEntries.stream().filter(accessTokenEntry -> accessTokenEntry.getRefreshToken().equals(clientInformation.getRefreshToken())).findFirst();

            if (first.isEmpty()) {
                throw new IllegalStateException(String.format("Refresh-Token %s not found.", clientInformation.getRefreshToken()));
            }

            if (!first.get().getClientId().equals(clientInformation.getClientId())) {
                // TODO Eintrag aus Datenbank löschen
                throw new IllegalStateException("Client Id passt nicht zum Refresh Token");
            }

            //TODO Handling für alte Token
            StringBuilder accessTokenScope = new StringBuilder();
            first.get().getScope().forEach(part -> accessTokenScope.append(part + " "));

            TokenResponse tokenResponse = getTokenResponse(clientInformation, accessTokenScope.toString());

            return tokenResponse;


        }else {
            new IllegalStateException(String.format("Unknown grant type %s.", clientInformation.getGrantType()));
        }

        return null;
    }

    private TokenResponse getTokenResponse(ClientInformation clientInformation, String accessTokenScope) throws IOException {
        String accessToken = RandomStringUtils.random(8, true, true);
        String refreshToken = RandomStringUtils.random(8, true, true);

        log.info(String.format("Issuing access token %s with scope %s", accessToken, accessTokenScope));

        String str = accessToken + " " + clientInformation.getClientId() + " " + refreshToken + " " + accessTokenScope + System.lineSeparator();
        Files.write(Paths.get("accessTokens.txt"), str.getBytes(), StandardOpenOption.APPEND);

        TokenResponse tokenResponse = new TokenResponse(accessToken, TOKENTYPE, refreshToken);
        return tokenResponse;
    }
}
