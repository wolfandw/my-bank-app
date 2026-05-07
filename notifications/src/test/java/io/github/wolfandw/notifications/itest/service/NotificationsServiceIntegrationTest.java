package io.github.wolfandw.notifications.itest.service;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.notifications.itest.BaseNotificationsIntegrationTest;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Интеграционный тест сервиса уведомлений.
 */
public class NotificationsServiceIntegrationTest extends BaseNotificationsIntegrationTest {
    @ParameterizedTest
    @ValueSource(strings = {"accounts-to-notifications", "cash-to-notifications", "transfer-to-notifications"})
    void listenTest(String topic) {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Outbox outbox = new Outbox();
        outbox.setId(outboxId);
        outbox.setUserId(outboxId);
        outbox.setMessage("test message, топик: " + topic);

        SenderRecord<UUID, Outbox, UUID> record =
                SenderRecord.create(new ProducerRecord<>(topic, outbox.getId(), outbox), outbox.getId());
        trxStepVerifier.create(kafkaSenderTest.send(Flux.just(record))).
            consumeNextWith(actualSenderResult -> {
                assertThat(actualSenderResult.correlationMetadata()).isEqualTo(outboxId);
            }).verifyComplete();
    }

    @Test
    void requestNotificationTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        trxStepVerifier.create(notificationsService.requestNotification(outboxId, outboxId, "test message")).
                consumeNextWith(actualOutboxId -> {
                    assertThat(actualOutboxId).isEqualTo(outboxId);
                }).verifyComplete();
    }
}
