package sia.tcloud3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sia.tcloud3.entity.TacoOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface OrderRepository extends JpaRepository<TacoOrder, Long> {
    List<TacoOrder> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}
