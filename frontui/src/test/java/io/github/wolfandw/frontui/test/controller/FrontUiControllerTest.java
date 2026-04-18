package io.github.wolfandw.frontui.test.controller;

import io.github.wolfandw.chassis.dto.*;
import io.github.wolfandw.frontui.controller.FrontUiController;
import io.github.wolfandw.frontui.service.FrontUiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульные тесты контроллера ui.
 */
@ExtendWith(MockitoExtension.class)
class FrontUiControllerTest {
    @Mock
    private FrontUiService frontUiService;

    @InjectMocks
    private FrontUiController frontUiController;

    @Test
    void getAccountIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        when(frontUiService.getAccount()).thenReturn(Mono.empty());

        StepVerifier.create(frontUiController.getAccount("error", "info")).verifyComplete();
    }

    @Test
    void getAccountTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID adminId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        UserDto userDto = new UserDto(userId, "user", "User", "1999-01-01");
        UserDto adminDto = new UserDto(adminId, "admin", "Admin", "1999-01-01");
        AccountDto accountDto = new AccountDto(userId, userDto, BigDecimal.TEN,  List.of(adminDto));
        when(frontUiService.getAccount()).thenReturn(Mono.just(accountDto));

        StepVerifier.create(frontUiController.getAccount("error", "info"))
                .consumeNextWith(actualRendering -> {
                    assertThat(actualRendering).isNotNull();
                }).verifyComplete();
    }

    @Test
    void changeCashIsUnauthorizedTest() {
        OperationResultDto operationResultDto = new OperationResultDto(new UUID(0,0), "user", false, "error message");
        when(frontUiService.changeCash(any(BigDecimal.class), any(CashAction.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(frontUiController.changeCash(new ChangeCashRequestDto("user", BigDecimal.TEN, CashAction.PUT)))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult).isEqualTo("redirect:/account?error=error+message");
                }).verifyComplete();
    }

    @Test
    void changeCashTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(frontUiService.changeCash(any(BigDecimal.class), any(CashAction.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(frontUiController.changeCash(new ChangeCashRequestDto("user", BigDecimal.TEN, CashAction.PUT)))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult).isEqualTo("redirect:/account?info=test+message");
                }).verifyComplete();
    }

    @Test
    void transferCashIsUnauthorizedTest() {
        OperationResultDto operationResultDto = new OperationResultDto(new UUID(0,0), "user", false, "error message");
        when(frontUiService.transferCash(any(BigDecimal.class), any(String.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(frontUiController.transferCash(new TransferCashRequestDto("user", BigDecimal.TEN, "admin")))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult).isEqualTo("redirect:/account?error=error+message");
                }).verifyComplete();
    }

    @Test
    void transferCashTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(frontUiService.transferCash(any(BigDecimal.class), any(String.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(frontUiController.transferCash(new TransferCashRequestDto("user", BigDecimal.TEN, "admin")))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult).isEqualTo("redirect:/account?info=test+message");
                }).verifyComplete();
    }

    @Test
    void changeUserDataIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(new UUID(0,0), "user", false, "error message");
        when(frontUiService.changeUserData(any(String.class), any(LocalDate.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(frontUiController.changeUserData(new ChangeUserDataRequestDto("user", "User", LocalDate.of(1999, 1, 1))))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult).isEqualTo("redirect:/account?error=error+message");
                }).verifyComplete();
    }

    @Test
    void changeUserDataTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(frontUiService.changeUserData(any(String.class), any(LocalDate.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(frontUiController.changeUserData(new ChangeUserDataRequestDto("user", "User", LocalDate.of(1999, 1, 1))))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult).isEqualTo("redirect:/account?info=test+message");
                }).verifyComplete();
    }
}
