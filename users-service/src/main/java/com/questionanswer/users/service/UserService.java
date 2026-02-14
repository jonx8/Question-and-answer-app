package com.questionanswer.users.service;

import com.questionanswer.users.dto.RegisterRequest;
import com.questionanswer.users.dto.UserProfileResponse;

import java.util.UUID;

public interface UserService {

    UserProfileResponse registerUser(RegisterRequest request);

    UserProfileResponse getUserProfile(UUID userId);
}