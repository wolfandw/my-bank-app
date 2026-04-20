package io.github.wolfandw.transfer.test.contoller;

import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.dto.TransferCashRequestDto;
import io.github.wolfandw.transfer.controller.TransferController;
import io.github.wolfandw.transfer.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульные тесты контроллера переводов.
 */
@ExtendWith(MockitoExtension.class)
class TransferControllerTest {
    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    @Test
    void transferIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(new UUID(0,0), "user", false, "error message");
        when(transferService.transferCash(any(String.class), any(BigDecimal.class), any(String.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(transferController.transfer(new TransferCashRequestDto("user", BigDecimal.TEN, "recipient"), createToken(userId)))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    @Test
    @WithMockUser(roles = "NOTIFICATIONS_SERVICE_CLIENT")
    void transferNotificationTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(transferService.transferCash(any(String.class), any(BigDecimal.class), any(String.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(transferController.transfer(new TransferCashRequestDto("user", BigDecimal.TEN, "recipient"), createToken(userId)))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    private Jwt createToken(UUID userId) {
        return Jwt.withTokenValue("custom-token")
                .header("alg", "none")
                .header("sub", userId.toString())
                .claim("preferred_username", "user")
                .build();
    }
}
