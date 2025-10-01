package com.questionanswer.users.mapper;

import com.questionanswer.users.controller.dto.CreateUserDto;
import com.questionanswer.users.controller.dto.UpdateUserDto;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserRepresentation toUserRepresentation(CreateUserDto request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(true);
        return user;
    }

    public UserRepresentation toUserRepresentation(UpdateUserDto request) {
        UserRepresentation user = new UserRepresentation();
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        return user;
    }
}