package sia.tcloud3.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sia.tcloud3.repositories.UserRepository;
//import sia.tcloud3.service.UserServiceImpl;

@Configuration
public class ApplicationConfiguration {

    private final UserRepository userRepository;


//    private final UserServiceImpl userService;

    public ApplicationConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return  username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationConfiguration config)
//        throws Exception {
//        return config.getAuthenticationManager();
//    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity,
                                                BCryptPasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception{
        AuthenticationManagerBuilder authenticationManagerBuilder =
                httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }

//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
//        return new ProviderManager(Arrays.asList(authenticationProvider));
//    }

//    @Bean
//    AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//
//        authProvider.setUserDetailsService(userDetailsService());
//        authProvider.setPasswordEncoder(passwordEncoder());
//
//        return authProvider;
//    }
}
