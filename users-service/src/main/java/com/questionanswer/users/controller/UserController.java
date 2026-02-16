package com.questionanswer.users.controller;

import com.questionanswer.users.dto.RegisterRequest;
import com.questionanswer.users.dto.UserProfileResponse;
import com.questionanswer.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@SecurityRequirement(name = "keycloak")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public ResponseEntity<UserProfileResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        UserProfileResponse userProfile = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userProfile);
    }

    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(JwtAuthenticationToken accessToken) {
        UserProfileResponse userProfile = userService.getUserProfile(UUID.fromString(accessToken.getName()));
        return ResponseEntity.ok(userProfile);
    }

    @Operation(summary = "Get user profile by ID")
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfileById(@PathVariable UUID userId) {
        UserProfileResponse userProfile = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }
}