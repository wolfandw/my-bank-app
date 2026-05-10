package io.github.wolfandw.cash.itest.repository;

import io.github.wolfandw.cash.itest.BaseCashIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

/**
 * Интеграционные тесты репозитория исходящих сообщений.
 */
public class OutboxRepositoryIntegrationTest extends BaseCashIntegrationTest {
    @BeforeEach
    void setUp() {
        outboxRepository.deleteAll()
                .thenMany(Flux.fromIterable(getTestOutboxes()))
                .concatMap(outbox -> outboxRepository.save(outbox))
                .delaySubscription(Duration.ofMillis(50))
                .blockLast();
    }

    @Test
    void findAllBySentFalseTest() {
        StepVerifier.create(outboxRepository.findAllBySent(false).collectList()).
                assertNext(actualOutboxes -> {
                    Assertions.assertThat(actualOutboxes).size().isEqualTo(3);
                    Assertions.assertThat(actualOutboxes.get(0).getSent()).isFalse();
                    Assertions.assertThat(actualOutboxes.get(1).getSent()).isFalse();
                    Assertions.assertThat(actualOutboxes.get(2).getSent()).isFalse();
                }).verifyComplete();
    }

    @Test
    void findAllBySentTrueTest() {
        StepVerifier.create(outboxRepository.findAllBySent(true).collectList()).
                assertNext(actualOutboxes -> {
                    Assertions.assertThat(actualOutboxes).size().isEqualTo(3);
                    Assertions.assertThat(actualOutboxes.get(0).getSent()).isTrue();
                    Assertions.assertThat(actualOutboxes.get(1).getSent()).isTrue();
                    Assertions.assertThat(actualOutboxes.get(2).getSent()).isTrue();
                }).verifyComplete();
    }

    @Test
    void deleteAllBySentTest() {
        StepVerifier.create(outboxRepository.deleteAllBySent(true)).expectNextCount(0).verifyComplete();
    }
}