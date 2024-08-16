package sia.tcloud3.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sia.tcloud3.repositories.UserRepository;
import sia.tcloud3.dtos.requests.UserUpdateRequest;
import sia.tcloud3.dtos.response.GetUserResponse;
import sia.tcloud3.entity.Users;
import sia.tcloud3.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CleanUpService cleanUpService;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,
                       CleanUpService cleanUpService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.cleanUpService = cleanUpService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Users> allUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public GetUserResponse getUser(Long userId) {
        return userMapper.toGetUserResponse(
                userRepository.findById(userId).orElseThrow(RuntimeException::new));
    }

    // TODO: Implement a method to clean up after user has been deleted,
    //
    @PreAuthorize("authentication.authenticated == true")
    public void deleteMyAccount() {
        Long userId = retrieveCurrentUser().getId();
        userRepository.deleteById(userId);
        cleanUpAfterDeletion(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserAccount(Long userId) {
        userRepository.deleteById(userId);
        cleanUpAfterDeletion(userId);
    }

    private void cleanUpAfterDeletion(Long userId) {
        cleanUpService.cleanUp(userId);
    }

    // TODO: Check this method. This should be a PatchMapping
    // TODO: Check how to map with Mapstruct for a patchmapping, only map when the request.parameter != null
    //  check how to implement the above using mapstruct.

    public GetUserResponse updateUser(UserUpdateRequest request) {
        Users user = retrieveCurrentUser();
        userMapper.updateUser(user, request);
        if (request.getPassword() != null)
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userMapper.toGetUserResponse(userRepository.save(user));
    }

    public Users retrieveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Users) authentication.getPrincipal();
    }

    public GetUserResponse currentUser() {
        return userMapper.toGetUserResponse(retrieveCurrentUser());
    }

    public Users getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User was not found."));
    }
}
