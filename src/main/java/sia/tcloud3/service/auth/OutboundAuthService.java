package sia.tcloud3.service.auth;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sia.tcloud3.configs.GoogleIdAuthenticationToken;
import sia.tcloud3.dtos.response.LoginResponse;
import sia.tcloud3.entity.Users;

import java.io.IOException;

import static sia.tcloud3.constants.APIConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboundAuthService {

    // ----------------------------------------------- Injected Beans ------------------------------------------------
    private final RestTemplate restTemplate;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;
    private final RefreshTokenService refreshTokenService;

    // ---------------------------------------------------------------------------------------------------------------


    @Value("${outbound.identity.client-id}")
    private String clientId;

    @Value("${outbound.identity.client-secret}")
    private String clientSecret;

    @Value("${outbound.identity.redirect-uri}")
    private String redirectUri;

    @Value("${security.jwt.expiration-time}")
    private Long expiry;


    private String getGoogleOauth2AccessToken(String code, String scope) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));

        HttpEntity<MultiValueMap<String, String>> requestEntity = getMultiValueMap(code, headers);
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        String response = restTemplate.postForObject(GOOGLE_OAUTH2_URL, requestEntity, String.class);
        log.info("Google OAuth2 response: {}", response);
        return response;
    }

    private HttpEntity<MultiValueMap<String, String>> getMultiValueMap(String code, HttpHeaders headers) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("scope", GOOGLE_USER_EMAIL_SCOPE);
        params.add("scope", GOOGLE_USER_PROFILE_SCOPE);
        params.add("scope", "openid");
        params.add("grant_type", "authorization_code");

        return new HttpEntity<>(params, headers);
    }


    public LoginResponse resolveUser(String code, String scope, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String oauth2response = getGoogleOauth2AccessToken(code, scope);
        JsonObject oauth2object = new Gson().fromJson(oauth2response, JsonObject.class);
        String accessToken = String.valueOf(oauth2object.get("access_token"));

        String token = String.valueOf(oauth2object.get("id_token"));
        if (token == null)
            return null;

        Object details = authenticationDetailsSource.buildDetails(request);
        GoogleIdAuthenticationToken authRequest = new GoogleIdAuthenticationToken(token, details);

        assert authenticationManager != null;
        Authentication authResult;
        try {
            authResult = authenticationManager.authenticate(authRequest);
        } catch (AuthenticationException e) {
            log.info("AuthenticationException occurred ", e);
            throw new IllegalArgumentException(e.getMessage(), e.getCause());
        }

        GoogleIdAuthenticationToken googleIdAuthenticationToken = (GoogleIdAuthenticationToken) authResult;
        String jwtToken = (String) googleIdAuthenticationToken.getCredentials();
        String refreshToken = refreshTokenService.createRefreshToken((Users) googleIdAuthenticationToken.getPrincipal());

        return LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(expiry)
                .refreshToken(refreshToken)
                .build();

//        resolveId(idToken, request, response);
    }

    // TODO: Do something with the json object in the future
    private void resolveId(String idToken, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/auth/outbound/resolveId?googleIdToken=" + idToken);
        dispatcher.forward(request, response);
    }

    private void accessScopes(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(GOOGLE_OAUTH2_USERINFO_URL, HttpMethod.GET, requestEntity, String.class);
        JsonObject jsonObject = new Gson().fromJson(response.getBody(), JsonObject.class);
        // TODO: Do something with this json object.
        log.info("Returned object: {}", jsonObject);
    }
}
