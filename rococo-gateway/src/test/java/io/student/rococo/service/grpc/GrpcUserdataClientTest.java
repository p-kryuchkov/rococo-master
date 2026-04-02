package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.UpdateUserRequest;
import io.student.rococo.grpc.UserResponse;
import io.student.rococo.grpc.UserdataReadServiceGrpc;
import io.student.rococo.grpc.UsernameRequest;
import io.student.rococo.model.EventJson;
import io.student.rococo.model.UserJson;
import io.student.rococo.utils.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static io.student.rococo.model.EventType.GET;
import static io.student.rococo.model.EventType.UPDATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GrpcUserdataClientTest {

    private static final UUID USER_ID = UUID.randomUUID();

    private final String USERNAME = "splinter";
    private final String FIRSTNAME = "Hamato";
    private final String LASTNAME = "Yoshi";

    private final KafkaTemplate<String, EventJson> kafkaTemplate = mock(KafkaTemplate.class);
    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final UserdataReadServiceGrpc.UserdataReadServiceBlockingStub stub =
            mock(UserdataReadServiceGrpc.UserdataReadServiceBlockingStub.class);

    private final GrpcUserdataClient client = new GrpcUserdataClient(kafkaTemplate, currentUserProvider);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "stub", stub);
        when(currentUserProvider.getUsername()).thenReturn(USERNAME);
    }

    @Test
    void getUserByUsername() {
        final UserResponse response = UserResponse.newBuilder()
                .setId(USER_ID.toString())
                .setUsername(USERNAME)
                .setFirstname(FIRSTNAME)
                .setLastname(LASTNAME)
                .build();

        when(stub.getUserByUsername(any(UsernameRequest.class))).thenReturn(response);

        UserJson result = client.getUserByUsername(USERNAME);

        assertNotNull(result);
        assertEquals(USER_ID, result.id());
        assertEquals(USERNAME, result.username());
        assertEquals(FIRSTNAME, result.firstname());
        assertEquals(LASTNAME, result.lastname());

        ArgumentCaptor<UsernameRequest> requestCaptor = ArgumentCaptor.forClass(UsernameRequest.class);
        verify(stub).getUserByUsername(requestCaptor.capture());
        assertEquals(USERNAME, requestCaptor.getValue().getUsername());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get User by Username", event.description());
        assertEquals(USER_ID, event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void getUserByUsernameShouldThrowBadRequestWhenUsernameIsNull() {
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.getUserByUsername(null));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("username is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getUserByUsernameShouldThrowBadRequestWhenUsernameIsBlank() {
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.getUserByUsername(" "));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("username is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getUserByUsernameShouldThrowGrpcStatusException() {
        when(stub.getUserByUsername(any(UsernameRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        assertThrows(GrpcStatusException.class, () -> client.getUserByUsername(USERNAME));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void updateUserWithAvatar() {
        UserJson user = mock(UserJson.class);

        when(user.id()).thenReturn(USER_ID);
        when(user.username()).thenReturn(USERNAME);
        String updatedFirstname = "Casey";
        when(user.firstname()).thenReturn(updatedFirstname);
        String updatedLastname = "Jones";
        when(user.lastname()).thenReturn(updatedLastname);

        byte[] avatarBytes = "avatar-image".getBytes(StandardCharsets.UTF_8);
        String base64Avatar = "data:image/png;base64," + Base64.getEncoder().encodeToString(avatarBytes);
        when(user.avatar()).thenReturn(base64Avatar);

        final UserResponse response = UserResponse.newBuilder()
                .setId(USER_ID.toString())
                .setUsername(USERNAME)
                .setFirstname(updatedFirstname)
                .setLastname(updatedLastname)
                .build();

        when(stub.updateUser(any(UpdateUserRequest.class))).thenReturn(response);

        UserJson result = client.updateUser(user);

        assertNotNull(result);
        assertEquals(USER_ID, result.id());
        assertEquals(USERNAME, result.username());
        assertEquals(updatedFirstname, result.firstname());
        assertEquals(updatedLastname, result.lastname());

        ArgumentCaptor<UpdateUserRequest> requestCaptor = ArgumentCaptor.forClass(UpdateUserRequest.class);
        verify(stub).updateUser(requestCaptor.capture());

        UpdateUserRequest request = requestCaptor.getValue();
        assertEquals(USER_ID.toString(), request.getId());
        assertEquals(USERNAME, request.getUsername());
        assertEquals(updatedFirstname, request.getFirstname());
        assertEquals(updatedLastname, request.getLastname());
        assertEquals(ByteString.copyFrom(avatarBytes), request.getAvatar());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(UPDATE, event.eventType());
        assertEquals("Update user", event.description());
        assertEquals(USER_ID, event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void updateUserWithoutAvatar() {
        UserJson user = mock(UserJson.class);

        when(user.id()).thenReturn(USER_ID);
        when(user.username()).thenReturn(USERNAME);
        when(user.firstname()).thenReturn(FIRSTNAME);
        when(user.lastname()).thenReturn(LASTNAME);
        when(user.avatar()).thenReturn(null);

        final UserResponse response = UserResponse.newBuilder()
                .setId(USER_ID.toString())
                .setUsername(USERNAME)
                .setFirstname(FIRSTNAME)
                .setLastname(LASTNAME)
                .build();

        when(stub.updateUser(any(UpdateUserRequest.class))).thenReturn(response);

        client.updateUser(user);

        ArgumentCaptor<UpdateUserRequest> requestCaptor = ArgumentCaptor.forClass(UpdateUserRequest.class);
        verify(stub).updateUser(requestCaptor.capture());

        UpdateUserRequest request = requestCaptor.getValue();
        assertEquals(USER_ID.toString(), request.getId());
        assertEquals(USERNAME, request.getUsername());
        assertEquals(FIRSTNAME, request.getFirstname());
        assertEquals(LASTNAME, request.getLastname());
        assertTrue(request.getAvatar().isEmpty());
    }

    @Test
    void updateUserShouldThrowBadRequestWhenUsernameIsNull() {
        UserJson user = mock(UserJson.class);
        when(user.username()).thenReturn(null);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.updateUser(user));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Username is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void updateUserShouldThrowBadRequestWhenUsernameIsBlank() {
        UserJson user = mock(UserJson.class);
        when(user.username()).thenReturn(" ");

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.updateUser(user));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Username is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void updateUserShouldThrowGrpcStatusException() {
        UserJson user = mock(UserJson.class);

        when(user.id()).thenReturn(USER_ID);
        when(user.username()).thenReturn(USERNAME);
        when(user.firstname()).thenReturn(FIRSTNAME);
        when(user.lastname()).thenReturn(LASTNAME);
        when(user.avatar()).thenReturn(null);

        when(stub.updateUser(any(UpdateUserRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        assertThrows(GrpcStatusException.class, () -> client.updateUser(user));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }
}