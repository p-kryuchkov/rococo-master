package io.student.rococo.service.db;

import io.student.rococo.data.entity.UserDataEntity;
import io.student.rococo.data.repository.UserRepository;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserdataDbServiceTest {
    private final String username = "splinter";
    private final String firstname = "Hamato";
    private final String lastname = "Yoshi";
    private final UUID id = UUID.randomUUID();
    private final byte[] avatar = {1, 2, 3};

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserdataDbService userdataDbService;

    @Test
    void getByUsernameShouldReturnUser() {
        final UserDataEntity user = new UserDataEntity();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        final UserDataEntity result = userdataDbService.getByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertSame(user, result);
    }

    @Test
    void getByUsernameShouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        final UserNotFoundException ex = assertThrows(
                UserNotFoundException.class,
                () -> userdataDbService.getByUsername(username)
        );

        assertEquals("User not found by username: " + username, ex.getMessage());
    }

    @Test
    void createUserShouldSaveUser() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        userdataDbService.createUser(username, firstname, lastname, avatar);

        final ArgumentCaptor<UserDataEntity> captor = ArgumentCaptor.forClass(UserDataEntity.class);
        verify(userRepository).save(captor.capture());

        final UserDataEntity saved = captor.getValue();

        assertNotNull(saved);
        assertEquals(username, saved.getUsername());
        assertEquals(firstname, saved.getFirstname());
        assertEquals(lastname, saved.getLastname());
        assertArrayEquals(avatar, saved.getAvatar());
    }

    @Test
    void createUserShouldSaveUserWithoutAvatar() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        userdataDbService.createUser(username, firstname, lastname, null);

        final ArgumentCaptor<UserDataEntity> captor = ArgumentCaptor.forClass(UserDataEntity.class);
        verify(userRepository).save(captor.capture());

        final UserDataEntity saved = captor.getValue();

        assertNotNull(saved);
        assertEquals(username, saved.getUsername());
        assertEquals(firstname, saved.getFirstname());
        assertEquals(lastname, saved.getLastname());
        assertNull(saved.getAvatar());
    }

    @Test
    void createUserShouldThrowWhenUsernameIsNull() {
        final FieldValidationException ex = assertThrows(
                FieldValidationException.class,
                () -> userdataDbService.createUser(null, "Leo", "Blue", null)
        );

        assertEquals("Username must not be null", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void createUserShouldThrowWhenUsernameExists() {
        final UserDataEntity user = new UserDataEntity();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        final FieldValidationException ex = assertThrows(
                FieldValidationException.class,
                () -> userdataDbService.createUser(username, "Mike", "Orange", null)
        );

        assertEquals("This username exists: " + username, ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserShouldUpdateAllFields() {
        final UserDataEntity user = new UserDataEntity();
        user.setId(id);
        user.setUsername(username);
        user.setFirstname("Casey");
        user.setLastname("Jones");
        user.setAvatar(new byte[]{4, 5, 6});

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        final UserDataEntity result = userdataDbService.updateUser(username, firstname, lastname, avatar);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(firstname, result.getFirstname());
        assertEquals(lastname, result.getLastname());
        assertArrayEquals(avatar, result.getAvatar());

        verify(userRepository).findByUsername(username);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserShouldUpdateOnlyFirstname() {
        final UserDataEntity user = new UserDataEntity();
        user.setId(id);
        user.setUsername(username);
        user.setFirstname("Casey");
        user.setLastname(lastname);
        user.setAvatar(avatar);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        final UserDataEntity result = userdataDbService.updateUser(username, firstname, "", null);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(firstname, result.getFirstname());
        assertEquals(lastname, result.getLastname());
        assertArrayEquals(avatar, result.getAvatar());

        verify(userRepository).findByUsername(username);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserShouldUpdateOnlyLastname() {
        final UserDataEntity user = new UserDataEntity();
        user.setId(id);
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname("Jones");
        user.setAvatar(avatar);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        final UserDataEntity result = userdataDbService.updateUser(username, "", lastname, null);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(firstname, result.getFirstname());
        assertEquals(lastname, result.getLastname());
        assertArrayEquals(avatar, result.getAvatar());

        verify(userRepository).findByUsername(username);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserShouldUpdateOnlyAvatar() {
        final byte[] newAvatar = {7, 8, 9};

        final UserDataEntity user = new UserDataEntity();
        user.setId(id);
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setAvatar(avatar);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        final UserDataEntity result = userdataDbService.updateUser(username, "", "", newAvatar);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(firstname, result.getFirstname());
        assertEquals(lastname, result.getLastname());
        assertArrayEquals(newAvatar, result.getAvatar());

        verify(userRepository).findByUsername(username);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserShouldNotUpdateFieldsWhenNamesBlankAndAvatarNull() {
        final UserDataEntity user = new UserDataEntity();
        user.setId(id);
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setAvatar(avatar);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        final UserDataEntity result = userdataDbService.updateUser(username, "", "", null);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(firstname, result.getFirstname());
        assertEquals(lastname, result.getLastname());
        assertArrayEquals(avatar, result.getAvatar());

        verify(userRepository).findByUsername(username);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserShouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        final UserNotFoundException ex = assertThrows(
                UserNotFoundException.class,
                () -> userdataDbService.updateUser(username, firstname, lastname, avatar)
        );

        assertEquals("User not found by username: " + username, ex.getMessage());
        verify(userRepository).findByUsername(username);
        verify(userRepository, never()).save(any());
    }
}