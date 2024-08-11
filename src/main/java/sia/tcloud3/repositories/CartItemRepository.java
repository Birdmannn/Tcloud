package sia.tcloud3.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sia.tcloud3.entity.CartItem;

@Repository
public interface CartItemRepository extends CrudRepository<CartItem, Long> {
}
