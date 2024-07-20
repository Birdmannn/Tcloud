package sia.tcloud3.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sia.tcloud3.repositories.UserRepository;
import sia.tcloud3.dtos.requests.LoginRequest;
import sia.tcloud3.dtos.requests.SignUpRequest;
import sia.tcloud3.dtos.response.LoginResponse;
import sia.tcloud3.entity.RefreshToken;
import sia.tcloud3.entity.Users;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final InMemoryTokenBlacklistService inMemoryTokenBlacklistService;
    private final UserService userService;

    private List<String> refreshTokenList;

    @Getter
    private String message = null;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService,
                                 JwtService jwtService, InMemoryTokenBlacklistService inMemoryTokenBlacklistService, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.inMemoryTokenBlacklistService = inMemoryTokenBlacklistService;
        this.userService = userService;
        refreshTokenList = new ArrayList<>();
    }

    public Users signUp(@NotNull SignUpRequest input) {
        Users user = new Users(input.getEmail(), passwordEncoder.encode(input.getPassword()),
                input.getFirstName(), input.getLastName(), Users.Role.USER);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            message = "User Exists.";
            return null;
        }
        message = "User registered successfully";
        return userRepository.save(user);
    }

    private Users authenticate(@NotNull LoginRequest input) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
        } catch (AuthenticationException e) {
            log.info("AuthenticationException occurred ", e);
            throw new UsernameNotFoundException("Invalid Username or password.");
        }

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public LoginResponse refreshToken(String token) {

        return refreshTokenService.findByToken(token)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    Users user = userService.getUserById(userId);
                    String accessToken = jwtService.generateToken(user);
                    return LoginResponse.builder()
                            .token(accessToken)
                            .refreshToken(refreshTokenService.createRefreshToken(user))
                            .build();
                }).orElseThrow(() -> new RuntimeException("Refresh Token is not in DB!"));
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Users authenticatedUser = authenticate(loginRequest);

        log.info("at /login: username: {}", authenticatedUser.getEmail()); // exists
        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = refreshTokenService.createRefreshToken(authenticatedUser);

        return LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getJwtExpiration())
                .refreshToken(refreshToken)
                .build();
    }

    public String logout(HttpServletRequest request) {
        // Collect a @RequestBody of the refresh token
        String token = extractTokenFromRequest(request);
        if (token  == null)
            return "No token detected. User is still logged in.";
        inMemoryTokenBlacklistService.addToBlacklist(token);
//        refreshTokenService.delete(userService.retrieveCurrentUser());

        // TODO: clear any session-related repositories if necessary
        return "Logged out Successfully";
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        @NotNull String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer "))
            return authorizationHeader.split(" ")[1];
        return null; // if not valid.
    }
}
