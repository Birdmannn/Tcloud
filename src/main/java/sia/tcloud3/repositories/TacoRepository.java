package sia.tcloud3.repositories;

import sia.tcloud3.entity.Taco;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface TacoRepository extends JpaRepository<Taco, Long> {

	Iterable<Taco> findByName(String name);

	List<Taco> findAllByUserId(Long id);

	void deleteAllByUserId(Long id);
}
