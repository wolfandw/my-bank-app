package io.github.wolfandw.cash.itest.service;

import io.github.wolfandw.cash.itest.BaseCashIntegrationTest;
import io.github.wolfandw.chassis.model.Outbox;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Интеграционный тест процессора исходящих сообщений.
 */
@ExtendWith(MockitoExtension.class)
public class OutboxProcessorServiceIntegrationTest extends BaseCashIntegrationTest {
    @BeforeEach
    void setUp() {
        outboxRepository.deleteAll()
                .thenMany(Flux.fromIterable(getTestOutboxes()))
                .concatMap(outbox -> outboxRepository.save(outbox))
                .delaySubscription(Duration.ofMillis(50))
                .blockLast();
    }

    @Test
    void sendTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Outbox outbox = new Outbox();
        outbox.setId(outboxId);
        outbox.setUserLogin("user");
        outbox.setMessage("test message");

        SenderRecord<UUID, Outbox, UUID> record =
                SenderRecord.create(new ProducerRecord<>(cashTopic, outbox.getId(), outbox), outbox.getId());
        StepVerifier.create(kafkaSender.send(Flux.just(record))).
                consumeNextWith(actualSenderResult -> {
                    assertThat(actualSenderResult.correlationMetadata()).isEqualTo(outboxId);
                }).verifyComplete();
    }

    @Test
    void processSendingUnsentOutboxTest() {
        StepVerifier.create(outboxProcessorService.processSendingUnsentOutbox().collectList()).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.get(0).getMessage().startsWith("test message")).isTrue();
                }).verifyComplete();
    }

    @Test
    void processDeletingSentOutboxTest() {
        StepVerifier.create(outboxProcessorService.processDeletingSentOutbox()).verifyComplete();
    }
}
