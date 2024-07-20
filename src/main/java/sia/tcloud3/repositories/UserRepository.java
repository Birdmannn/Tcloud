package sia.tcloud3.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sia.tcloud3.entity.Users;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<Users, Long> {

	Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String username);

    boolean existsByRole(Users.Role role);

}
