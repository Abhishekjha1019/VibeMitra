package com.vibemitra.user;

import com.vibemitra.auth.City;
import com.vibemitra.auth.InvalidCredentialsException;
import com.vibemitra.common.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public UserResponse getAccount(UUID userId) {
        return UserResponse.from(findActive(userId));
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
        User user = findActive(userId);
        String displayName = request.displayName() == null ? null : request.displayName().trim();
        String city = request.city() == null ? null : request.city().name();
        user.updateProfile(displayName, city, clock.instant());
        return UserResponse.from(user);
    }

    /**
     * Deletes the caller's own account after re-checking the password, so a stolen access token
     * alone cannot delete it. Personal data is erased. The existing token stops working because
     * the account is no longer active.
     */
    @Transactional
    public void deleteAccount(UUID userId, String password) {
        User user = findActive(userId);
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Password is incorrect");
        }
        String tombstoneEmail = "deleted-" + user.getId() + "@deleted.invalid";
        // A placeholder that is not a valid bcrypt hash, so no password can ever match it.
        user.markDeleted(tombstoneEmail, "!deleted", clock.instant());
    }

    private User findActive(UUID userId) {
        return userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }
}
