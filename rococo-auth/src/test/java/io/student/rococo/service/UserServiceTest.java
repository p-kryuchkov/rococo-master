package io.student.rococo.service;

import io.student.rococo.data.Authority;
import io.student.rococo.data.AuthorityEntity;
import io.student.rococo.data.UserEntity;
import io.student.rococo.data.repository.UserRepository;
import io.student.rococo.model.UserJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KafkaTemplate<String, UserJson> kafkaTemplate;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserAndReturnUsername() {
        when(passwordEncoder.encode("12345")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String result = userService.registerUser("testuser", "12345");

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());

        UserEntity savedUser = userCaptor.getValue();

        assertThat(result).isEqualTo("testuser");
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getEnabled()).isTrue();
        assertThat(savedUser.getAccountNonExpired()).isTrue();
        assertThat(savedUser.getAccountNonLocked()).isTrue();
        assertThat(savedUser.getCredentialsNonExpired()).isTrue();

        verify(passwordEncoder).encode("12345");
    }

    @Test
    void shouldAddReadAndWriteAuthorities() {
        when(passwordEncoder.encode("12345")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.registerUser("testuser", "12345");

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());

        UserEntity savedUser = userCaptor.getValue();

        assertThat(savedUser.getAuthorities())
                .extracting(AuthorityEntity::getAuthority)
                .containsExactlyInAnyOrder(Authority.read, Authority.write);
    }

    @Test
    void shouldSendKafkaMessageWithUsername() {
        when(passwordEncoder.encode("12345")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.registerUser("testuser", "12345");

        ArgumentCaptor<UserJson> userJsonCaptor = ArgumentCaptor.forClass(UserJson.class);
        verify(kafkaTemplate).send(eq("users"), userJsonCaptor.capture());

        UserJson sentMessage = userJsonCaptor.getValue();

        assertThat(sentMessage.username()).isEqualTo("testuser");
    }
}