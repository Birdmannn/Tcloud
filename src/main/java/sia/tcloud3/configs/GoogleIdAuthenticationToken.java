package sia.tcloud3.configs;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class GoogleIdAuthenticationToken extends AbstractAuthenticationToken {
    private final String credentials;
    private Object principal;

    public GoogleIdAuthenticationToken(String token, Object details) {
        super(new ArrayList<>());
        credentials = token;
        setDetails(details);
        setAuthenticated(false);
    }

    GoogleIdAuthenticationToken(String token, Object principal,
                                Collection<? extends GrantedAuthority> authorities, Object details) {
        super(authorities);
        credentials = token;
        this.principal = principal;
        setDetails(details);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
