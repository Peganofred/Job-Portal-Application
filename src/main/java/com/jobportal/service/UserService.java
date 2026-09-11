package com.jobportal.service;

import com.jobportal.dto.UpdateUserRequest;
import com.jobportal.dto.UserResponse;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.jobportal.exception.ResourceNotFoundException(
                        "User not found with id: " + userId));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.jobportal.exception.ResourceNotFoundException(
                        "User not found with id: " + userId));

        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }
}