package com.questionanswer.users.repository;

import com.questionanswer.users.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByEmail(String email);

    Optional<UserProfile> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}