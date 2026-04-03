package io.student.rococo.util;

import com.google.protobuf.ByteString;
import io.student.rococo.data.entity.UserDataEntity;
import io.student.rococo.grpc.UserResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseUtilTest {

    private final UUID id = UUID.randomUUID();
    private final String username = "splinter";
    private final String firstname = "Hamato";
    private final String lastname = "Yoshi";
    private final byte[] avatar = {1, 2, 3};

    @Test
    void mapUserWithAllFields() {
        final UserDataEntity user = new UserDataEntity();
        user.setId(id);
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setAvatar(avatar);

        final UserResponse result = UserResponseUtil.userEntityToUserProtoResponse(user);

        assertNotNull(result);
        assertEquals(id.toString(), result.getId());
        assertEquals(username, result.getUsername());
        assertEquals(firstname, result.getFirstname());
        assertEquals(lastname, result.getLastname());
        assertArrayEquals(avatar, result.getAvatar().toByteArray());
    }

    @Test
    void mapUserWithNullOptionalFields() {
        final UserDataEntity user = new UserDataEntity();
        user.setId(id);
        user.setUsername(username);
        user.setFirstname(null);
        user.setLastname(null);
        user.setAvatar(null);

        final UserResponse result = UserResponseUtil.userEntityToUserProtoResponse(user);

        assertNotNull(result);
        assertEquals(id.toString(), result.getId());
        assertEquals(username, result.getUsername());
        assertEquals("", result.getFirstname());
        assertEquals("", result.getLastname());
        assertEquals(ByteString.EMPTY, result.getAvatar());
    }
}