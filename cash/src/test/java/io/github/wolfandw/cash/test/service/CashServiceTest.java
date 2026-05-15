package io.github.wolfandw.cash.test.service;

import io.github.wolfandw.cash.service.impl.CashServiceImpl;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Модульный тест сервиса наличных.
 */
@ExtendWith(MockitoExtension.class)
public class CashServiceTest {
    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private Tracer tracer;

    @InjectMocks
    private CashServiceImpl cashService;

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Test
    void cashCashIsUnauthorizedTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Outbox outbox = new Outbox();
        outbox.setId(outboxId);
        outbox.setUserId(outboxId);
        outbox.setMessage("test message");
        when(outboxRepository.save(any(Outbox.class)))
                .thenReturn(Mono.just(outbox));

        OperationResultDto operationResultDto = new OperationResultDto(new UUID(0,0), "user", false, "error message");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(String.class), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OperationResultDto.class))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(cashService.changeCash("user", BigDecimal.TEN, CashAction.PUT)).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    @Test
    void cashCashNotificationTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Outbox outbox = new Outbox();
        outbox.setId(outboxId);
        outbox.setUserId(outboxId);
        outbox.setMessage("test message");
        when(outboxRepository.save(any(Outbox.class)))
                .thenReturn(Mono.just(outbox));

        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(String.class), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OperationResultDto.class))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(cashService.changeCash("user", BigDecimal.TEN, CashAction.PUT)).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }
}
