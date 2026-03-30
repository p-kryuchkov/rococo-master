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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("12345");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("read");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> databaseUserDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Username: `unknown` not found");
    }

    @Test
    void shouldReturnMultipleAuthorities() {
        AuthorityEntity writeAuthority = new AuthorityEntity();
        writeAuthority.setAuthority(Authority.write);
        userEntity.addAuthorities(writeAuthority);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(userEntity));

        UserDetails userDetails = databaseUserDetailsService.loadUserByUsername("testuser");

        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("read", "write");
    }

    @Test
    void shouldReturnDisabledUser() {
        userEntity.setEnabled(false);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(userEntity));

        UserDetails userDetails = databaseUserDetailsService.loadUserByUsername("testuser");

        assertThat(userDetails.isEnabled()).isFalse();
    }

    @Test
    void shouldReturnLockedUser() {
        userEntity.setAccountNonLocked(false);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(userEntity));

        UserDetails userDetails = databaseUserDetailsService.loadUserByUsername("testuser");
        assertThat(userDetails.isAccountNonLocked()).isFalse();
    }

    @Test
    void shouldReturnExpiredAccount() {
        userEntity.setAccountNonExpired(false);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(userEntity));

        UserDetails userDetails = databaseUserDetailsService.loadUserByUsername("testuser");

        assertThat(userDetails.isAccountNonExpired()).isFalse();
    }

    @Test
    void shouldReturnExpiredCredentials() {
        userEntity.setCredentialsNonExpired(false);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(userEntity));

        UserDetails userDetails = databaseUserDetailsService.loadUserByUsername("testuser");

        assertThat(userDetails.isCredentialsNonExpired()).isFalse();
    }
}