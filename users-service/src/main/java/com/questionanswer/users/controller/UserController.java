package com.questionanswer.users.controller;


import com.questionanswer.users.controller.dto.CreateUserDto;
import com.questionanswer.users.controller.dto.PasswordResetDto;
import com.questionanswer.users.controller.dto.UpdateUserDto;
import com.questionanswer.users.mapper.UserMapper;
import com.questionanswer.users.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@SecurityRequirement(name = "keycloak")
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserRepresentation>> getAllUsers() {
        List<UserRepresentation> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserRepresentation> getUserById(@PathVariable String userId) {
        try {
            UserRepresentation user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/email")
    public ResponseEntity<List<UserRepresentation>> findUserByEmail(@RequestParam String email) {
        List<UserRepresentation> users = userService.findUserByEmail(email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/username")
    public ResponseEntity<List<UserRepresentation>> findUserByUsername(@RequestParam String username) {
        List<UserRepresentation> users = userService.findUserByUsername(username);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDto request) {
        try {
            UserRepresentation user = userMapper.toUserRepresentation(request);
            UserRepresentation createdUser = userService.createUser(user);

            userService.resetPassword(createdUser.getId(), request.password());

            return ResponseEntity.status(201).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserDto request) {
        try {
            UserRepresentation user = userMapper.toUserRepresentation(request);
            UserRepresentation updatedUser = userService.updateUser(userId, user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable String userId,
            @Valid @RequestBody PasswordResetDto request) {
        try {
            userService.resetPassword(userId, request.newPassword());
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> userExists(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username) {
        boolean exists = false;

        if (email != null) {
            exists = !userService.findUserByEmail(email).isEmpty();
        } else if (username != null) {
            exists = !userService.findUserByUsername(username).isEmpty();
        }

        return ResponseEntity.ok(Map.of("exists", exists));
    }
}