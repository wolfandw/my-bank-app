package io.github.wolfandw.accounts.itest.repository;

import io.github.wolfandw.accounts.itest.BaseAccountsIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Интеграционные тесты репозитория счетов.
 */
public class AccountRepositoryIntegrationTest extends BaseAccountsIntegrationTest {
    @Test
    void findByUserIdTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        trxStepVerifier.create(accountRepository.findByUserId(userId)).
                assertNext(actualAccount -> {
                    assertThat(actualAccount.getId()).isEqualTo(UUID.fromString("650e8400-e29b-41d4-a716-446655440000"));
                }).verifyComplete();
    }
}