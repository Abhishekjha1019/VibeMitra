package com.vibemitra.user;

import com.vibemitra.auth.City;
import com.vibemitra.common.NotFoundException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-27T10:00:00Z");
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);

    private User existingUser(UUID id) {
        return new User(id, "asha@example.com", "hash", "Asha", LocalDate.of(1995, 5, 1),
                "DELHI", Instant.parse("2026-01-01T00:00:00Z"));
    }

    @Test
    void updatesOnlyTheFieldsThatArePresent() {
        UUID id = UUID.randomUUID();
        UserRepository repo = mock(UserRepository.class);
        when(repo.findById(id)).thenReturn(Optional.of(existingUser(id)));
        UserService service = new UserService(repo, clock);

        UserResponse response = service.updateProfile(id,
                new UpdateProfileRequest("  Asha K  ", null));

        assertThat(response.displayName()).isEqualTo("Asha K");
        assertThat(response.city()).isEqualTo("DELHI");
    }

    @Test
    void changesCityToAnotherLaunchCity() {
        UUID id = UUID.randomUUID();
        UserRepository repo = mock(UserRepository.class);
        when(repo.findById(id)).thenReturn(Optional.of(existingUser(id)));
        UserService service = new UserService(repo, clock);

        UserResponse response = service.updateProfile(id, new UpdateProfileRequest(null, City.GURUGRAM));

        assertThat(response.city()).isEqualTo("GURUGRAM");
        assertThat(response.displayName()).isEqualTo("Asha");
    }

    @Test
    void rejectsAnEmptyUpdate() {
        UserService service = new UserService(mock(UserRepository.class), clock);

        assertThatThrownBy(() -> service.updateProfile(UUID.randomUUID(), new UpdateProfileRequest(null, null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reportsMissingAccount() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.findById(any())).thenReturn(Optional.empty());
        UserService service = new UserService(repo, clock);

        assertThatThrownBy(() -> service.updateProfile(UUID.randomUUID(), new UpdateProfileRequest("Asha", null)))
                .isInstanceOf(NotFoundException.class);
    }
}
