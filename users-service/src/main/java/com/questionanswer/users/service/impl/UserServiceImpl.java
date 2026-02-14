package com.questionanswer.users.service.impl;

import com.questionanswer.users.dto.RegisterRequest;
import com.questionanswer.users.dto.UserProfileResponse;
import com.questionanswer.users.entity.UserProfile;
import com.questionanswer.users.exception.ProfileAlreadyExistsException;
import com.questionanswer.users.exception.ProfileNotFoundException;
import com.questionanswer.users.mapper.UserMapper;
import com.questionanswer.users.repository.UserProfileRepository;
import com.questionanswer.users.service.KeycloakService;
import com.questionanswer.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final KeycloakService keycloakService;
    private final UserProfileRepository userProfileRepository;

    @Override
    public UserProfileResponse registerUser(RegisterRequest request) {
        // Fast checks without transaction for UX
        if (userProfileRepository.existsByEmail(request.email())) {
            throw new ProfileAlreadyExistsException("Email already in use: " + request.email());
        }
        if (userProfileRepository.existsByUsername(request.username())) {
            throw new ProfileAlreadyExistsException("Username already in use: " + request.username());
        }

        UserRepresentation keycloakUser = keycloakService.createUser(request);

        try {
            UserProfile userProfile = userMapper.toEntity(keycloakUser);
            userProfile = userProfileRepository.save(userProfile);
            return userMapper.toResponse(userProfile);
        } catch (Exception e) {
            log.error("Error occurred while saving user profile to DB: {}. Trying to delete user {} in keycloak", e.getMessage(), keycloakUser.getId());
            keycloakService.deleteUserWithRetry(keycloakUser.getId());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userId) {
        return userProfileRepository.findById(userId).map(userMapper::toResponse)
                .orElseThrow(() -> ProfileNotFoundException.withId(userId));
    }

}