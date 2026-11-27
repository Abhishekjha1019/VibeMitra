package com.vibemitra.user;

import com.vibemitra.auth.City;
import com.vibemitra.auth.InvalidCredentialsException;
import com.vibemitra.common.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    private static final String PASSWORD = "correct-horse-battery";
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);

    private User existingUser(UUID id) {
        return new User(id, "asha@example.com", encoder.encode(PASSWORD), "Asha",
                LocalDate.of(1995, 5, 1), "DELHI", Instant.parse("2026-01-01T00:00:00Z"));
    }

    private UserService service(UserRepository repo) {
        return new UserService(repo, encoder, clock);
    }

    @Test
    void updatesOnlyTheFieldsThatArePresent() {
        UUID id = UUID.randomUUID();
        UserRepository repo = mock(UserRepository.class);
        when(repo.findById(id)).thenReturn(Optional.of(existingUser(id)));

        UserResponse response = service(repo).updateProfile(id, new UpdateProfileRequest("  Asha K  ", null));

        assertThat(response.displayName()).isEqualTo("Asha K");
        assertThat(response.city()).isEqualTo("DELHI");
    }

    @Test
    void changesCityToAnotherLaunchCity() {
        UUID id = UUID.randomUUID();
        UserRepository repo = mock(UserRepository.class);
        when(repo.findById(id)).thenReturn(Optional.of(existingUser(id)));

        UserResponse response = service(repo).updateProfile(id, new UpdateProfileRequest(null, City.GURUGRAM));

        assertThat(response.city()).isEqualTo("GURUGRAM");
        assertThat(response.displayName()).isEqualTo("Asha");
    }

    @Test
    void rejectsAnEmptyUpdate() {
        UserService service = service(mock(UserRepository.class));

        assertThatThrownBy(() -> service.updateProfile(UUID.randomUUID(), new UpdateProfileRequest(null, null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reportsMissingAccount() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service(repo).updateProfile(UUID.randomUUID(), new UpdateProfileRequest("Asha", null)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deletingAccountErasesPersonalDataAndDeactivatesIt() {
        UUID id = UUID.randomUUID();
        User user = existingUser(id);
        UserRepository repo = mock(UserRepository.class);
        when(repo.findById(id)).thenReturn(Optional.of(user));

        service(repo).deleteAccount(id, PASSWORD);

        assertThat(user.getStatus()).isEqualTo("DELETED");
        assertThat(user.isActive()).isFalse();
        assertThat(user.getEmail()).isEqualTo("deleted-" + id + "@deleted.invalid");
        assertThat(user.getDisplayName()).isEqualTo("Deleted user");
        assertThat(user.getDateOfBirth()).isEqualTo(LocalDate.of(1970, 1, 1));
        assertThat(encoder.matches(PASSWORD, user.getPasswordHash())).isFalse();
    }

    @Test
    void wrongPasswordCannotDeleteAccount() {
        UUID id = UUID.randomUUID();
        User user = existingUser(id);
        UserRepository repo = mock(UserRepository.class);
        when(repo.findById(id)).thenReturn(Optional.of(user));
        UserService service = service(repo);

        assertThatThrownBy(() -> service.deleteAccount(id, "wrong-password"))
                .isInstanceOf(InvalidCredentialsException.class);
        assertThat(user.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void deletedAccountIsTreatedAsMissing() {
        UUID id = UUID.randomUUID();
        User user = existingUser(id);
        user.markDeleted("deleted-" + id + "@deleted.invalid", "!deleted", NOW);
        UserRepository repo = mock(UserRepository.class);
        when(repo.findById(id)).thenReturn(Optional.of(user));
        UserService service = service(repo);

        assertThatThrownBy(() -> service.getAccount(id)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> service.deleteAccount(id, PASSWORD)).isInstanceOf(NotFoundException.class);
    }
}
