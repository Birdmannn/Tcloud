package sia.tcloud3.repositories;

import sia.tcloud3.entity.TacoOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface OrderRepository extends CrudRepository<TacoOrder, Long> {
    List<TacoOrder> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}
