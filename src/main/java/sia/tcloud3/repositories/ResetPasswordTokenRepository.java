package sia.tcloud3.repositories;

import org.springframework.data.repository.CrudRepository;
import sia.tcloud3.entity.ResetPasswordToken;

import java.util.Optional;

public interface ResetPasswordTokenRepository extends CrudRepository<ResetPasswordToken, Long> {
    Optional<ResetPasswordToken> findByToken(String token);
}
