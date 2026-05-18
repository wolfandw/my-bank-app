package io.github.wolfandw.cash.test.service;

import io.github.wolfandw.chassis.metric.BusinessMetricIncrementor;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.chassis.service.OutboxProcessorService;
import io.github.wolfandw.chassis.service.impl.OutboxProcessorServiceImpl;
import io.micrometer.tracing.Tracer;
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
 * Модульный тест сервиса наличных.
 */
@ExtendWith(MockitoExtension.class)
public class OutboxProcessorServiceTest {
    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private KafkaSender<UUID, Outbox> kafkaSender;

    @Mock
    private Tracer tracer;

    @Mock
    private SenderResult<UUID> senderResult;

    @Mock
    private BusinessMetricIncrementor businessMetricIncrementor;

    private OutboxProcessorService outboxProcessorService;

    @BeforeEach
    void setUp() {
        outboxProcessorService = new OutboxProcessorServiceImpl(kafkaSender,
                "${spring.kafka.topics.topic}",
                outboxRepository, businessMetricIncrementor);
    }

    @Test
    void processDeletingSentOutboxTest() {
        when(outboxRepository.deleteAllBySent(any(Boolean.class))).thenReturn(Mono.empty());
        StepVerifier.create(outboxProcessorService.processDeletingSentOutbox()).verifyComplete();
        verify(outboxRepository, times(1)).deleteAllBySent(any(Boolean.class));
    }

    @Test
    void processSendingUnsentOutboxTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Outbox outbox = new Outbox();
        outbox.setId(outboxId);
        outbox.setUserId("user");
        outbox.setMessage("test message");
        when(outboxRepository.findAllBySent(any(Boolean.class)))
                .thenReturn(Flux.just(outbox));

        when(outboxRepository.findById(any(UUID.class)))
                .thenReturn(Mono.just(outbox));

        when(outboxRepository.save(any(Outbox.class)))
                .thenReturn(Mono.just(outbox));

        when(senderResult.correlationMetadata()).thenReturn(outboxId);

        when(kafkaSender.send(any(Publisher.class))).thenReturn(Flux.just(senderResult));

        StepVerifier.create(outboxProcessorService.processSendingUnsentOutbox().collectList()).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.get(0).getId()).isEqualTo(outboxId);
                }).verifyComplete();
    }
}
