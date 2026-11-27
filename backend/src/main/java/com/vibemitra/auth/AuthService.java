package com.vibemitra.auth;

import com.vibemitra.common.ConflictException;
import com.vibemitra.security.JwtService;
import com.vibemitra.user.User;
import com.vibemitra.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {

    /** Returned for both unknown email and wrong password, so accounts cannot be enumerated. */
    static final String INVALID_CREDENTIALS = "Invalid email or password";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Clock clock;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.clock = clock;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        LocalDate today = LocalDate.now(clock);

        if (!AgeRule.isAdult(request.dateOfBirth(), today)) {
            throw new IllegalArgumentException("You must be at least 18 years old to use VibeMitra");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("An account with this email already exists");
        }

        UUID id = UUID.randomUUID();
        User user = new User(
                id,
                email,
                passwordEncoder.encode(request.password()),
                request.displayName().trim(),
                request.dateOfBirth(),
                request.city().name(),
                clock.instant());
        userRepository.save(user);

        return new AuthResponse(jwtService.issueToken(id), id);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.email()))
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }
        return new AuthResponse(jwtService.issueToken(user.getId()), user.getId());
    }

    static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
