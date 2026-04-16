package io.github.wolfandw.transfer.test.service;

import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.transfer.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульный тест сервиса переводов.
 */
@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {
    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    @Test
    void transferCashIsUnauthorizedTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Outbox outbox = new Outbox();
        outbox.setId(outboxId);
        outbox.setUserId(outboxId);
        outbox.setMessage("test message");
        when(outboxRepository.save(any(Outbox.class)))
                .thenReturn(Mono.just(outbox));

        OperationResultDto operationResultDto = new OperationResultDto(new UUID(0,0), "user", false, "error message");
        when(webClient.post()
                .uri(any(Function.class))
                .retrieve()
                .bodyToMono(OperationResultDto.class))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(transferService.transferCash("user", BigDecimal.TEN, "recipient")).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    @Test
    void transferCashNotificationTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Outbox outbox = new Outbox();
        outbox.setId(outboxId);
        outbox.setUserId(outboxId);
        outbox.setMessage("test message");
        when(outboxRepository.save(any(Outbox.class)))
                .thenReturn(Mono.just(outbox));

        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");
        when(webClient.post()
                .uri(any(Function.class))
                .retrieve()
                .bodyToMono(OperationResultDto.class))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(transferService.transferCash("user", BigDecimal.TEN, "recipient")).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }
}
