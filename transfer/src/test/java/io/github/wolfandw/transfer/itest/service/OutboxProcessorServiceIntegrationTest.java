package io.github.wolfandw.transfer.itest.service;

import io.github.wolfandw.transfer.itest.BaseTransferIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Интеграционный тест процессора исходящих сообщений.
 */
@ExtendWith(MockitoExtension.class)
public class OutboxProcessorServiceIntegrationTest extends BaseTransferIntegrationTest {
    @Test
    void processSendingUnsentOutboxTest() {
        trxStepVerifier.create(outboxProcessorService.processSendingUnsentOutbox().collectList()).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.get(0).getMessage().startsWith("test message")).isTrue();
                }).verifyComplete();
    }

//    @Test
//    void processDeletingSentOutboxTest() {
//        trxStepVerifier.create(outboxProcessorService.processDeletingSentOutbox()).verifyComplete();
//    }
}
