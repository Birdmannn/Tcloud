package sia.tcloud3.configs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import sia.tcloud3.repositories.UserRepository;
import sia.tcloud3.entity.Users;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_EMAIL = "admin@gmail.com";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        log.info("Initializing application....");

        return args -> {
            if (! userRepository.existsByRole(Users.Role.ADMIN)) {
                Users user = Users.builder()
                        .firstName("Cullo")
                        .lastName("Cullo")
                        .email(ADMIN_EMAIL)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .role(Users.Role.ADMIN)
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password: admin. please change it.");
            }
            log.info("Application initialization completed.");
        };
    }
}
