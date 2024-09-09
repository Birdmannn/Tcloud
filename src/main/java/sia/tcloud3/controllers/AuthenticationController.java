package sia.tcloud3.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.tcloud3.dtos.requests.RefreshTokenRequest;
import sia.tcloud3.dtos.requests.ResetPasswordRequest;
import sia.tcloud3.dtos.response.LoginResponse;
import sia.tcloud3.dtos.requests.LoginRequest;
import sia.tcloud3.dtos.requests.SignUpRequest;
import sia.tcloud3.entity.Users;
import sia.tcloud3.service.auth.AuthenticationService;

import java.io.IOException;
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
        LoginResponse loginResponse = authenticationService.login(loginRequest, false);
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

    @GetMapping("/signup/confirm")
    public String confirm(@RequestParam("token") String token) {
        return authenticationService.confirmToken(token);
    }

    @PostMapping("forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        String response = authenticationService.forgotPassword(email);
        return response == null ? ResponseEntity.badRequest().body("Invalid request. Check your email address and try again.")
                : ResponseEntity.ok(response);
    }

    @GetMapping("resetPassword")
    public String resetPassword(@RequestParam("token") String token) {
        boolean value = authenticationService.resetPassword(token);
        return value ? "Confirmed" : "Invalid Token";
    }

    @PostMapping("resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request,
                                                @RequestParam("token") String token) {
        String response = authenticationService.resetPassword(request, token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/outbound/authenticate")   // Or any response object, let's see
    public ResponseEntity<LoginResponse> grantCode(@RequestParam("code") String code, @RequestParam("scope") String scope,
                          HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LoginResponse loginResponse = authenticationService.processCode(code, scope, request, response);
        return ResponseEntity.ok(loginResponse);
    }
}
