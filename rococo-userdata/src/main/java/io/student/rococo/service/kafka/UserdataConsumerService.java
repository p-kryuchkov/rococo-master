package io.student.rococo.service.kafka;

import io.student.rococo.model.UserJson;
import io.student.rococo.service.db.UserdataDbService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserdataConsumerService {
    private final UserdataDbService userdataDbService;

    @Autowired
    public UserdataConsumerService(UserdataDbService userdataDbService) {
        this.userdataDbService = userdataDbService;
    }

    @Transactional
    @KafkaListener(topics = "users", groupId = "userdata")
    public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
        userdataDbService.createUser(user.username(), null, null, null);
    }
}
