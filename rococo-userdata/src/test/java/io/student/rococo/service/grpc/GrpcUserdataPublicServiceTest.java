package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.UserDataEntity;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.grpc.UpdateUserRequest;
import io.student.rococo.grpc.UserResponse;
import io.student.rococo.grpc.UsernameRequest;
import io.student.rococo.service.db.UserdataDbService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcUserdataPublicServiceTest {

    private final UUID id = UUID.randomUUID();
    private final String username = "splinter";
    private final String firstname = "Hamato";
    private final String lastname = "Yoshi";
    private final byte[] avatar = {1, 2, 3};

    @Mock
    private UserdataDbService userdataDbService;

    @Mock
    private StreamObserver<UserResponse> responseObserver;

    @InjectMocks
    private GrpcUserdataPublicService grpcUserdataPublicService;

    @Test
    void getUserByUsernameShouldReturnUser() {
        final UserDataEntity user = new UserDataEntity();
        user.setId(id);
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setAvatar(avatar);

        when(userdataDbService.getByUsername(username)).thenReturn(user);

        final UsernameRequest request = UsernameRequest.newBuilder()
                .setUsername(username)
                .build();

        grpcUserdataPublicService.getUserByUsername(request, responseObserver);

        final ArgumentCaptor<UserResponse> captor = ArgumentCaptor.forClass(UserResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        final UserResponse response = captor.getValue();

        assertNotNull(response);
        assertEquals(id.toString(), response.getId());
        assertEquals(username, response.getUsername());
        assertEquals(firstname, response.getFirstname());
        assertEquals(lastname, response.getLastname());
        assertArrayEquals(avatar, response.getAvatar().toByteArray());

        verify(userdataDbService).getByUsername(username);
    }

    @Test
    void updateUserShouldUpdateUser() {
        final UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setId(id.toString())
                .setUsername(username)
                .setFirstname(firstname)
                .setLastname(lastname)
                .setAvatar(ByteString.copyFrom(avatar))
                .build();

        final UserDataEntity updated = new UserDataEntity();
        updated.setId(id);
        updated.setUsername(username);
        updated.setFirstname(firstname);
        updated.setLastname(lastname);
        updated.setAvatar(avatar);

        when(userdataDbService.updateUser(eq(id), eq(username), eq(firstname), eq(lastname), any()))
                .thenReturn(updated);

        grpcUserdataPublicService.updateUser(request, responseObserver);

        final ArgumentCaptor<byte[]> avatarCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(userdataDbService).updateUser(
                eq(id),
                eq(username),
                eq(firstname),
                eq(lastname),
                avatarCaptor.capture()
        );

        assertArrayEquals(avatar, avatarCaptor.getValue());

        final ArgumentCaptor<UserResponse> responseCaptor = ArgumentCaptor.forClass(UserResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        final UserResponse response = responseCaptor.getValue();

        assertNotNull(response);
        assertEquals(id.toString(), response.getId());
        assertEquals(username, response.getUsername());
        assertEquals(firstname, response.getFirstname());
        assertEquals(lastname, response.getLastname());
        assertArrayEquals(avatar, response.getAvatar().toByteArray());
    }

    @Test
    void updateUserShouldSetNullAvatar() {
        final UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setId(id.toString())
                .setUsername(username)
                .setFirstname(firstname)
                .setLastname(lastname)
                .build();

        final UserDataEntity updated = new UserDataEntity();
        updated.setId(id);
        updated.setUsername(username);
        updated.setFirstname(firstname);
        updated.setLastname(lastname);
        updated.setAvatar(null);

        when(userdataDbService.updateUser(eq(id), eq(username), eq(firstname), eq(lastname), isNull()))
                .thenReturn(updated);

        grpcUserdataPublicService.updateUser(request, responseObserver);

        verify(userdataDbService).updateUser(
                eq(id),
                eq(username),
                eq(firstname),
                eq(lastname),
                isNull()
        );

        final ArgumentCaptor<UserResponse> captor = ArgumentCaptor.forClass(UserResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        final UserResponse response = captor.getValue();

        assertNotNull(response);
        assertEquals(id.toString(), response.getId());
        assertEquals(username, response.getUsername());
        assertEquals(firstname, response.getFirstname());
        assertEquals(lastname, response.getLastname());
        assertTrue(response.getAvatar().isEmpty());
    }

    @Test
    void updateUserShouldThrowWhenUsernameIsNull() {
        final UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setId(id.toString())
                .setFirstname(firstname)
                .setLastname(lastname)
                .build();

        final FieldValidationException ex = assertThrows(
                FieldValidationException.class,
                () -> grpcUserdataPublicService.updateUser(request, responseObserver)
        );

        assertEquals("Username must not be null", ex.getMessage());
        verifyNoInteractions(userdataDbService);
        verifyNoInteractions(responseObserver);
    }
}