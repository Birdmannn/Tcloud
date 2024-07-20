package sia.tcloud3.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sia.tcloud3.dtos.requests.UserUpdateRequest;
import sia.tcloud3.dtos.response.GetUserResponse;
import sia.tcloud3.entity.Users;
import sia.tcloud3.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping( "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Users>> allUsers() {
        List<Users> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<GetUserResponse> myProfile() {
        return ResponseEntity.ok(userService.currentUser());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResponse> getUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @DeleteMapping("/{userId}")
    public HttpStatus deleteUserById(@PathVariable("userId") Long userId) {
        userService.deleteUserAccount(userId);
        return HttpStatus.NO_CONTENT;
    }

    @DeleteMapping("/me")
    public HttpStatus deleteUser() {
        userService.deleteMyAccount();
        return HttpStatus.NO_CONTENT;
    }

    @PatchMapping("/me")        // Should be PatchMapping, no?
    public ResponseEntity<GetUserResponse> updateUser(@RequestBody UserUpdateRequest request) {
        GetUserResponse response = userService.updateUser(request);
        return ResponseEntity.accepted().body(response);
    }

}
