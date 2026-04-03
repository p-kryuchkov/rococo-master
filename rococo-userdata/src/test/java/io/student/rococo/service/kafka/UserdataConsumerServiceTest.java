package io.student.rococo.service.kafka;

import io.student.rococo.model.UserJson;
import io.student.rococo.service.db.UserdataDbService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserdataConsumerServiceTest {

    private final String username = "splinter";

    @Mock
    private UserdataDbService userdataDbService;

    @Mock
    private ConsumerRecord<String, UserJson> consumerRecord;

    @InjectMocks
    private UserdataConsumerService userdataConsumerService;

    @Test
    void listenerShouldCreateUser() {
        final UserJson user = new UserJson(username);

        userdataConsumerService.listener(user, consumerRecord);

        verify(userdataDbService).createUser(username, null, null, null);
    }
}