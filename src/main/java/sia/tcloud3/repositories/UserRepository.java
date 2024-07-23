package sia.tcloud3.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sia.tcloud3.entity.Users;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

	Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String username);

    boolean existsByRole(Users.Role role);

    @Transactional
    @Modifying
    @Query("UPDATE Users a SET a.enabled = true WHERE a.email = ?1")
    int enableUserByEmail(String email);

}
