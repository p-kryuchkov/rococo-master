package io.student.rococo.service;

import io.student.rococo.data.Authority;
import io.student.rococo.data.AuthorityEntity;
import io.student.rococo.data.UserEntity;
import io.student.rococo.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DatabaseUserDetailsService databaseUserDetailsService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        userEntity.setUsername("testuser");
        userEntity.setPassword("12345");
        userEntity.setEnabled(true);
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setCredentialsNonExpired(true);

        AuthorityEntity authorityEntity = new AuthorityEntity();
        authorityEntity.setAuthority(Authority.read);
        userEntity.addAuthorities(authorityEntity);
    }

    @Test
    void shouldLoadUserByUsername() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(userEntity));

        UserDetails userDetails = databaseUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("12345", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());

        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertEquals(1, authorities.size());
        assertEquals("read", authorities.get(0));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> databaseUserDetailsService.loadUserByUsername("unknown")
        );

        assertTrue(exception.getMessage().contains("Username: `unknown` not found"));
    }

    @Test
    void shouldReturnMultipleAuthorities() {
        AuthorityEntity writeAuthority = new AuthorityEntity();
        writeAuthority.setAuthority(Authority.write);
        userEntity.addAuthorities(writeAuthority);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(userEntity));

        UserDetails userDetails = databaseUserDetailsService.loadUserByUsername("testuser");

        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        assertEquals(2, authorities.size());
        assertIterableEquals(List.of("read", "write"), authorities);
    }

    @Test
    void shouldReturnDisabledUser() {
        userEntity.setEnabled(false);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(userEntity));

        UserDetails userDetails = databaseUserDetailsService.loadUserByUsername("testuser");

        assertFalse(userDetails.isEnabled());
    }

    @Test
    void shouldReturnLockedUser() {
        userEntity.setAccountNonLocked(false);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(userEntity));

        UserDetails userDetails = databaseUserDetailsService.loadUserByUsername("testuser");

        assertFalse(userDetails.isAccountNonLocked());
    }

    @Test
    void shouldReturnExpiredAccount() {
        userEntity.setAccountNonExpired(false);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(userEntity));

        UserDetails userDetails = databaseUserDetailsService.loadUserByUsername("testuser");

        assertFalse(userDetails.isAccountNonExpired());
    }

    @Test
    void shouldReturnExpiredCredentials() {
        userEntity.setCredentialsNonExpired(false);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(userEntity));

        UserDetails userDetails = databaseUserDetailsService.loadUserByUsername("testuser");

        assertFalse(userDetails.isCredentialsNonExpired());
    }
}