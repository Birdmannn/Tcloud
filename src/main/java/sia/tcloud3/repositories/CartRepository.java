package sia.tcloud3.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sia.tcloud3.entity.Cart;

import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
