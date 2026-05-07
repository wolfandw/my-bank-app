package io.github.wolfandw.cash.itest.service;

import io.github.wolfandw.cash.itest.BaseCashIntegrationTest;
import io.github.wolfandw.chassis.model.Outbox;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Интеграционный тест процессора исходящих сообщений.
 */
@ExtendWith(MockitoExtension.class)
public class OutboxProcessorServiceIntegrationTest extends BaseCashIntegrationTest {
    @Test
    void processSendingUnsentOutboxTest() {
        trxStepVerifier.create(outboxProcessorService.processSendingUnsentOutbox().collectList()).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.get(0).getMessage().startsWith("test message")).isTrue();
                }).verifyComplete();
    }

    @Test
    void processDeletingSentOutboxTest() {
        trxStepVerifier.create(outboxProcessorService.processDeletingSentOutbox()).verifyComplete();
    }
}
