package com.vibemitra.user;

import com.vibemitra.auth.City;
import com.vibemitra.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Clock clock;

    public UserService(UserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    /**
     * Updates the caller's own display name and/or city. The city must be one of the launch
     * cities (enforced by the City enum and the database CHECK constraint).
     */
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        if (request.displayName() == null && request.city() == null) {
            throw new IllegalArgumentException("Provide at least one field to update");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        String displayName = request.displayName() == null ? null : request.displayName().trim();
        String city = request.city() == null ? null : request.city().name();
        user.updateProfile(displayName, city, clock.instant());
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getAccount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return UserResponse.from(user);
    }
}
