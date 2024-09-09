package sia.tcloud3.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sia.tcloud3.repositories.auth.RefreshTokenRepository;
import sia.tcloud3.repositories.UserRepository;
import sia.tcloud3.entity.RefreshToken;
import sia.tcloud3.entity.Users;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RefreshTokenService {

    @Value("${security.jwt.refresh-expiration-time}")
    private int refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createRefreshToken(Users user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(refreshTokenExpiration, ChronoUnit.MILLIS))
                .userId(user.getId())
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            throw new RuntimeException(token.getToken() + " Refresh token is expired. " +
                    "Please make a new login..!");
        }
        refreshTokenRepository.delete(token);
        return token;
    }

    public void delete(Users user) {
        Optional<Iterable<RefreshToken>> refreshTokens = refreshTokenRepository.findAllByUserId(user.getId());
        if (! refreshTokens.isPresent())
            return;
        refreshTokenRepository.deleteAll(refreshTokens.get());
        log.info("Delete method EOL");
    }
}
