package io.github.wolfandw.cash.test.controller;

import io.github.wolfandw.cash.controller.CashController;
import io.github.wolfandw.cash.service.CashService;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.ChangeCashRequestDto;
import io.github.wolfandw.chassis.dto.OperationResultDto;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульные тесты контроллера наличных.
 */
@ExtendWith(MockitoExtension.class)
class CashControllerTest {
    @Mock
    private CashService cashService;

    @InjectMocks
    private CashController cashController;

    @Test
    void editCashIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(new UUID(0,0), "user", false, "error message");
        when(cashService.changeCash(any(String.class), any(BigDecimal.class), any(CashAction.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(cashController.editCash(new ChangeCashRequestDto("user", BigDecimal.TEN, CashAction.PUT), createToken(userId)))
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
        when(cashService.changeCash(any(String.class), any(BigDecimal.class), any(CashAction.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(cashController.editCash(new ChangeCashRequestDto("user", BigDecimal.TEN, CashAction.PUT), createToken(userId)))
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
