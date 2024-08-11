package sia.tcloud3.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sia.tcloud3.entity.ConfirmationToken;
import sia.tcloud3.repositories.UserRepository;
import sia.tcloud3.dtos.requests.LoginRequest;
import sia.tcloud3.dtos.requests.SignUpRequest;
import sia.tcloud3.dtos.response.LoginResponse;
import sia.tcloud3.entity.RefreshToken;
import sia.tcloud3.entity.Users;
import sia.tcloud3.service.email.EmailBuilder;
import sia.tcloud3.service.email.EmailService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;

    private List<String> refreshTokenList;


    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService,
                                 JwtService jwtService, InMemoryTokenBlacklistService inMemoryTokenBlacklistService, UserService userService, ConfirmationTokenService confirmationTokenService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.inMemoryTokenBlacklistService = inMemoryTokenBlacklistService;
        this.userService = userService;
        refreshTokenList = new ArrayList<>();
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
    }

    public Users signUp(@NotNull SignUpRequest input) {
        Users newUser = new Users(input.getEmail(), passwordEncoder.encode(input.getPassword()),
                input.getFirstName(), input.getLastName(), Users.Role.USER);
        newUser.setLocked(false);
        newUser.setEnabled(false);
        log.info("User {} enabled? {}", newUser.getEmail(), newUser.isEnabled());
        Optional<Users> optUser = userRepository.findByEmail(newUser.getEmail());
        log.info("user here");
        if (optUser.isPresent()) {
            Users user = optUser.get();
            if (user.isEnabled())
               return null;
            log.info("Reached here, user is not enabled");
        }
        else {
            userRepository.save(newUser);
        }

        Users user = optUser.orElse(newUser);
        String token = UUID.randomUUID().toString();
        saveConfirmationToken(user, token);

        // Here is for the email confirmation
        // Since we are running the spring boot app in localhost, we are hardcoding the url of the server
        // We are creating a POST request with token param
        log.info("sending link and token {}", token);
        String link = "http://localhost:8082/auth/signup/confirm?token=" + token;
//        emailService.sendEmail(user.getEmail(), EmailBuilder.buildEmail(user.getFirstName(), link));
        log.info("email sent. reached here.");
        log.info("link {}", link);
        return user;
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
        String token = extractTokenFromRequest(request);
        if (token  == null)
            return "No token detected. User is still logged in.";
        inMemoryTokenBlacklistService.addToBlacklist(token);
//        refreshTokenService.delete(userService.retrieveCurrentUser());

        // TODO: clear any session-related repositories if necessary
        return "Logged out Successfully";
    }

    public int enableUser(String email) {
        return userRepository.enableUserByEmail(email);
    }



    // ----------------------------------- Strictly Confirmation Token methods -------------------------------------------

    @Transactional
    public String confirmToken(String token) {
        Optional<ConfirmationToken> confirmationToken = confirmationTokenService.getToken(token);
        if (! confirmationToken.isPresent())
            throw new IllegalStateException("Token not found!");

        if (confirmationToken.get().getConfirmedAt() != null)
            throw new IllegalStateException("Email is already confirmed.");

        LocalDateTime expiresAt = confirmationToken.get().getExpiresAt();
        if (expiresAt.isBefore(LocalDateTime.now()))
            throw new IllegalStateException("Token is already expired.");

        confirmationTokenService.setConfirmedAt(token);
        enableUser(confirmationToken.get().getUser().getEmail());

        return "Your email is confirmed. Thank you for using our service!";
    }


    // ----------------------------------------- Private methods ----------------------------------------------------------

    private String extractTokenFromRequest(HttpServletRequest request) {
        @NotNull String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer "))
            return authorizationHeader.split(" ")[1];
        return null; // if not valid.
    }

    private void saveConfirmationToken(Users user, String token) {
        ConfirmationToken confirmationToken = ConfirmationToken.builder().token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user).build();
        confirmationTokenService.saveConfirmationToken(confirmationToken);
    }
}
