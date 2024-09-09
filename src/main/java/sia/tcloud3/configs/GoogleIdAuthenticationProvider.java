package sia.tcloud3.configs;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import sia.tcloud3.entity.Users;
import sia.tcloud3.repositories.UserRepository;
import sia.tcloud3.service.auth.JwtService;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleIdAuthenticationProvider implements AuthenticationProvider {

    @Getter
    @Setter
    @Value("${outbound.identity.client-id}")
    private String clientId;

    private final HttpTransport httpTransport = new ApacheHttpTransport();
    private final JsonFactory jsonFactory = new JacksonFactory();
    private final UserRepository userRepository;
    private final JwtService jwtService;

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            log.debug("This authentication provider does not support instances of type {}", authentication.getClass().getName());
            return null;
        }

        GoogleIdAuthenticationToken googleIdAuthenticationToken = (GoogleIdAuthenticationToken) authentication;
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();

        String token = (String) googleIdAuthenticationToken.getCredentials();
        token = token.substring(1, token.length() - 1);
        GoogleIdToken googleIdToken;

        try {
            googleIdToken = verifier.verify(token);
            if (googleIdToken == null)
                throw new BadCredentialsException("Unable to verify token -- Bad token or Invalid credentials.");
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException("Unable to verify token", e);
        }

        Payload payload = googleIdToken.getPayload();
        String email = payload.getEmail();

        UserDetails userDetails;

        while (true) {
            try {
                userDetails = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found."));
                if (!userDetails.isAccountNonLocked())
                    throw new LockedException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked",
                            "User account is locked."));
                if (!userDetails.isEnabled())
                    throw new DisabledException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled",
                            "User account is disabled."));
                if (!userDetails.isAccountNonExpired())
                    throw new AccountExpiredException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired",
                            "User account has expired."));
                break;
            } catch (UsernameNotFoundException e) {
                String firstName = (String) payload.get("given_name");
                String lastName = (String) payload.get("family_name");
                // TODO: Do something with this profile picture url in the future
                String pictureUrl = (String) payload.get("picture");

                Users user = Users.builder().email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .role(Users.Role.USER)
                        .enabled(true)
                        .locked(false).build();
                userRepository.save(user);
                log.info("User with email :{} is a new user. User has been saved.", email);
            }
        }

        String jwtToken = jwtService.generateToken(userDetails);
        return new GoogleIdAuthenticationToken(jwtToken,
                userDetails, userDetails.getAuthorities(), authentication.getDetails());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (GoogleIdAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
