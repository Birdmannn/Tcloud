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
import sia.tcloud3.dtos.requests.ResetPasswordRequest;
import sia.tcloud3.entity.ConfirmationToken;
import sia.tcloud3.entity.ResetPasswordToken;
import sia.tcloud3.repositories.ResetPasswordTokenRepository;
import sia.tcloud3.repositories.UserRepository;
import sia.tcloud3.dtos.requests.LoginRequest;
import sia.tcloud3.dtos.requests.SignUpRequest;
import sia.tcloud3.dtos.response.LoginResponse;
import sia.tcloud3.entity.RefreshToken;
import sia.tcloud3.entity.Users;
import sia.tcloud3.service.email.EmailService;

import java.time.Instant;
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
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    private List<String> refreshTokenList;
    private static final long EXPIRE_TOKEN = 15;


    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService,
                                 JwtService jwtService, InMemoryTokenBlacklistService inMemoryTokenBlacklistService,
                                 UserService userService, ConfirmationTokenService confirmationTokenService,
                                 EmailService emailService, ResetPasswordTokenRepository resetPasswordTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.inMemoryTokenBlacklistService = inMemoryTokenBlacklistService;
        this.userService = userService;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
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

    public void enableUser(String email) {
        userRepository.enableUserByEmail(email);
    }

    public String forgotPassword(String email) {
        Optional<Users> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent())
            return null;
        Users user = userOpt.get();
        String token = UUID.randomUUID().toString();
        ResetPasswordToken resetPasswordToken = ResetPasswordToken.builder()
                .token(token)
                .userId(user.getId())
                .expiresAt(Instant.now().plusSeconds(EXPIRE_TOKEN * 60)).build(); // 15 minutes
        resetPasswordTokenRepository.save(resetPasswordToken);
        String url = "http://localhost:8082/auth/resetPassword?token=" + token;
        String tempMessage = "Your Reset Password link: " + url + ". If you didn't request for this, please ignore this message.";
        log.info("Reset Password link: {}", url);
        // TODO: Implementation -- Send a link of the front-end reset password page to the user
        //  Then still check this...
//        emailService.sendEmail(email, tempMessage);
        return "Reset Password link has been sent to that email.";
    }

    public boolean resetPassword(String token) {
        Optional<ResetPasswordToken> resetPasswordToken = resetPasswordTokenRepository.findByToken(token);
        if (!resetPasswordToken.isPresent()) {
            log.warn("token not present.");
            return false;
        }
        ResetPasswordToken rt = resetPasswordToken.get();
        if (! rt.getExpiresAt().isBefore(Instant.now())) {
            rt.setEnabled(true);
            resetPasswordTokenRepository.save(rt);
            return true;
        }
        log.warn("Token is expired.");
        resetPasswordTokenRepository.delete(rt);
        return false;
    }

    public String resetPassword(ResetPasswordRequest request, @NotNull String token) {
        Optional<ResetPasswordToken> resetPasswordToken = resetPasswordTokenRepository.findByToken(token);
        if (resetPasswordToken.isPresent()) {
            ResetPasswordToken rt = resetPasswordToken.get();
            if (rt.isEnabled()) {
                Users user = userService.getUserById(rt.getUserId());
                log.info("User email: {}", user.getEmail());
                String password = request.getPassword();
                String confirmPassword = request.getConfirmPassword();
                log.info("password: {}", password);
                log.info("confirmPassword {}", confirmPassword);
                if (password.equals(confirmPassword)) {
                    user.setPassword(passwordEncoder.encode(password));
                    userRepository.save(user);
                    resetPasswordTokenRepository.delete(rt);        // the token should now be invalid
                    return "Successful";
                }
                return "Password Mismatch.";
            }
        }
        return "Invalid Token";
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
