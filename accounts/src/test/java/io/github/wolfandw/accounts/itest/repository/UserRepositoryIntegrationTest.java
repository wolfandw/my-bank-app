package io.github.wolfandw.accounts.itest.repository;

import io.github.wolfandw.accounts.itest.BaseAccountsIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Интеграционные тесты репозитория пользователей.
 */
public class UserRepositoryIntegrationTest extends BaseAccountsIntegrationTest {
    @Test
    void findByLoginTest() {
        trxStepVerifier.create(userRepository.findByLogin("user")).
                assertNext(actualUser -> {
                    assertThat(actualUser.getName()).isEqualTo("User");
                }).verifyComplete();
    }

    @Test
    void findAllBySentTrueTest() {
        trxStepVerifier.create(userRepository.findAllByLoginNot("user").collectList()).
                assertNext(actualUsers -> {
                    assertThat(actualUsers.size()).isEqualTo(1);
                    assertThat(actualUsers.get(0).getLogin()).isEqualTo("admin");
                }).verifyComplete();
    }
}