package io.github.wolfandw.accounts.itest.service;

import io.github.wolfandw.accounts.itest.BaseAccountsIntegrationTest;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.chassis.service.OutboxProcessorService;
import io.github.wolfandw.chassis.service.impl.OutboxProcessorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Интеграционный тест процессора исходящих сообщений.
 */
@ExtendWith(MockitoExtension.class)
public class OutboxProcessorServiceIntegrationTest extends BaseAccountsIntegrationTest {
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
