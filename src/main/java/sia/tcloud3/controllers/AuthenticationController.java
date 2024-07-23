package sia.tcloud3.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.tcloud3.dtos.requests.RefreshTokenRequest;
import sia.tcloud3.dtos.response.LoginResponse;
import sia.tcloud3.dtos.requests.LoginRequest;
import sia.tcloud3.dtos.requests.SignUpRequest;
import sia.tcloud3.entity.Users;
import sia.tcloud3.service.AuthenticationService;
//import sia.tcloud3.service.UserServiceImpl;

@Slf4j
@RestController
@RequestMapping(value = "/auth", produces = "application/json")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Users> register(@RequestBody SignUpRequest signUpRequest) {
        Users registeredUser = authenticationService.signUp(signUpRequest);
        if (registeredUser == null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authenticationService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody RefreshTokenRequest request) {
        LoginResponse loginResponse = authenticationService.refreshToken(request.getToken());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> authenticate(HttpServletRequest request) {
        String res = authenticationService.logout(request);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/signup/confirm")
    public String confirm(@RequestParam("token") String token) {
        return authenticationService.confirmToken(token);
    }
}
