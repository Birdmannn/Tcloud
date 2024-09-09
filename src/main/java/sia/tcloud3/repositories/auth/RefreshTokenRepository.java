package sia.tcloud3.repositories.auth;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sia.tcloud3.entity.RefreshToken;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    boolean deleteRefreshTokenByUserId(Long userId);
    Optional<Iterable<RefreshToken>> findAllByUserId(Long userId);

}
