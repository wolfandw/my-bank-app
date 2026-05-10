package io.github.wolfandw.notifications.itest.service;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.notifications.itest.BaseNotificationsIntegrationTest;
import io.github.wolfandw.notifications.model.Notification;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.*;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;

/**
 * Интеграционный тест сервиса уведомлений.
 */
public class NotificationsServiceIntegrationTest extends BaseNotificationsIntegrationTest {

    private static final int TIMEOUT = 30;
    private static String ACCOUNTS_TOPIC;
    private static String CASH_TOPIC;
    private static String TRANSFER_TOPIC;

    @BeforeAll
    static void setupAwaitility() {
        setDefaultTimeout(30, SECONDS);
        setDefaultPollInterval(fibonacci(MILLISECONDS));
        setDefaultPollDelay(100, MILLISECONDS);
    }

    @BeforeEach
    void setUp() {
        notificationsRepository.deleteAll()
                .thenMany(Flux.fromIterable(getTestNotifications()))
                .concatMap(outbox -> notificationsRepository.save(outbox))
                .delaySubscription(Duration.ofMillis(50))
                .blockLast();
    }

    @Value("${accounts.kafka.topic}")
    public void setAccountsTopic(String topic) {
        ACCOUNTS_TOPIC = topic;
    }

    @Value("${cash.kafka.topic}")
    public void setCashTopic(String topic) {
        CASH_TOPIC = topic;
    }

    static Stream<String> topicProvider() {
        return Stream.of(ACCOUNTS_TOPIC, CASH_TOPIC, TRANSFER_TOPIC);
    }

    @Value("${transfer.kafka.topic}")
    public void setTransferTopic(String topic) {
        TRANSFER_TOPIC = topic;
    }

    @ParameterizedTest
    @MethodSource("topicProvider")
    void listenTest(String topic) {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Outbox outbox = new Outbox();
        outbox.setId(outboxId);
        outbox.setUserId(outboxId);
        outbox.setSent(false);
        outbox.setMessage("test message, топик: " + topic);

        SenderRecord<UUID, Outbox, UUID> record =
                SenderRecord.create(new ProducerRecord<>(topic, outbox.getId(), outbox), outbox.getId());
        StepVerifier.create(kafkaSenderTest.send(Flux.just(record))).
                consumeNextWith(actualSenderResult -> {
                    assertThat(actualSenderResult.correlationMetadata()).isEqualTo(outboxId);
                }).verifyComplete();

        await()
                .atMost(TIMEOUT, SECONDS)
                .until(() -> {
                    Notification notification = notificationsRepository.findByOutboxIdAndSent(outboxId, false).block();
                    return notification != null && notification.getOutboxId().equals(outboxId) && !notification.getSent();
                });
    }

    @Test
    void processSendingUnsentNotificationTest() {
        StepVerifier.create(notificationsService.processSendUnsentNotifications().collectList()).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.get(0).getMessage().startsWith("test message")).isTrue();
                }).verifyComplete();
    }

    @Test
    void processDeletingSentNotificationTest() {
        StepVerifier.create(notificationsService.processDeleteSentNotifications()).verifyComplete();
    }
}
