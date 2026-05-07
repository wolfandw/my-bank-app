package io.github.wolfandw.accounts.itest.repository;

import io.github.wolfandw.accounts.itest.BaseAccountsIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Интеграционные тесты репозитория счетов.
 */
public class OutboxRepositoryIntegrationTest extends BaseAccountsIntegrationTest {
    @Test
    void findAllBySentFalseTest() {
        trxStepVerifier.create(outboxRepository.findAllBySent(false).collectList()).
                assertNext(actualNotifications -> {
                    Assertions.assertThat(actualNotifications).size().isEqualTo(3);
                    Assertions.assertThat(actualNotifications.get(0).getUserId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
                    Assertions.assertThat(actualNotifications.get(1).getUserId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"));
                    Assertions.assertThat(actualNotifications.get(2).getUserId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"));
                }).verifyComplete();
    }

    @Test
    void findAllBySentTrueTest() {
        trxStepVerifier.create(outboxRepository.findAllBySent(true).collectList()).
                assertNext(actualNotifications -> {
                    Assertions.assertThat(actualNotifications).size().isEqualTo(3);
                    Assertions.assertThat(actualNotifications.get(0).getUserId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440003"));
                    Assertions.assertThat(actualNotifications.get(1).getUserId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440004"));
                    Assertions.assertThat(actualNotifications.get(2).getUserId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440005"));
                }).verifyComplete();
    }

    @Test
    void deleteAllBySentTest() {
        trxStepVerifier.create(outboxRepository.deleteAllBySent(true)).expectNextCount(0).verifyComplete();
    }
}