package io.github.wolfandw.cash.itest.repository;

import io.github.wolfandw.cash.itest.BaseCashIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория исходящих сообщений.
 */
public class CashRepositoryIntegrationTest extends BaseCashIntegrationTest {
    @Test
    void findAllBySentFalseTest() {
        trxStepVerifier.create(outboxRepository.findAllBySent(false).collectList()).
                assertNext(actualNotifications -> {
                    assertThat(actualNotifications).size().isEqualTo(3);
                    assertThat(actualNotifications.get(0).getUserId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
                    assertThat(actualNotifications.get(1).getUserId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"));
                    assertThat(actualNotifications.get(2).getUserId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"));
                }).verifyComplete();
    }

    @Test
    void findAllBySentTrueTest() {
        trxStepVerifier.create(outboxRepository.findAllBySent(true).collectList()).
                assertNext(actualNotifications -> {
                    assertThat(actualNotifications).size().isEqualTo(3);
                    assertThat(actualNotifications.get(0).getUserId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440003"));
                    assertThat(actualNotifications.get(1).getUserId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440004"));
                    assertThat(actualNotifications.get(2).getUserId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440005"));
                }).verifyComplete();
    }

    @Test
    void deleteAllBySentTest() {
        trxStepVerifier.create(outboxRepository.deleteAllBySent(true)).expectNextCount(0).verifyComplete();
    }
}