package sia.tcloud3.service.auth;

import org.springframework.stereotype.Service;
import sia.tcloud3.entity.ConfirmationToken;
import sia.tcloud3.repositories.auth.ConfirmationTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmationTokenByConfirmedAt(token, LocalDateTime.now());
    }
}
