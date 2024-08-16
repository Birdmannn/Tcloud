package sia.tcloud3.repositories;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sia.tcloud3.entity.TacoOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface OrderRepository extends JpaRepository<TacoOrder, Long> {
    List<TacoOrder> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);

    @Query("SELECT o FROM TacoOrder o WHERE " +
            "LOWER(o.deliveryName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(o.deliveryStreet) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(o.deliveryCity) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(o.deliveryZip) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<TacoOrder> findBySearchText(String searchText);
}
