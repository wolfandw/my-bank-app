package io.github.wolfandw.transfer.itest.service;

import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.transfer.itest.BaseTransferIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Интеграционный тест сервиса переводов.
 */
public class TransferServiceIntegrationTest extends BaseTransferIntegrationTest {
    @MockitoBean
    private WebClient webClient;
    @MockitoBean
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @MockitoBean
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @MockitoBean
    private WebClient.ResponseSpec responseSpec;
    @MockitoBean
    private WebClient.RequestBodySpec requestBodySpec;

    @Test
    void transferCashIsUnauthorizedTest() {
        trxStepVerifier.create(transferService.transferCash("user", BigDecimal.TEN, "recipient"))
                .verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = {"USER", "TRANSFER_WRITE"})
    void requestNotificationTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OperationResultDto.class))
                .thenReturn(Mono.just(operationResultDto));

        trxStepVerifier.create(transferService.transferCash("user", BigDecimal.TEN, "recipient")).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.accepted()).isTrue();
                }).verifyComplete();
    }
}
