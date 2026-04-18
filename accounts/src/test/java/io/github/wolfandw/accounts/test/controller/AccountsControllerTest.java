package io.github.wolfandw.accounts.test.controller;

import io.github.wolfandw.accounts.controller.AccountsController;
import io.github.wolfandw.accounts.controller.UserController;
import io.github.wolfandw.accounts.service.AccountsService;
import io.github.wolfandw.accounts.service.UserService;
import io.github.wolfandw.chassis.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
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
 * Модульные тесты контроллера счетов.
 */
@ExtendWith(MockitoExtension.class)
class AccountsControllerTest {
    @Mock
    private AccountsService accountService;

    @InjectMocks
    private AccountsController accountController;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAccountIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        when(accountService.getAccount(any(String.class))).thenReturn(Mono.empty());

        StepVerifier.create(accountController.getAccount(createToken(userId))).verifyComplete();
    }

    @Test
    void getAccountTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID adminId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        UserDto userDto = new UserDto(userId, "user", "User", "1999-01-01");
        UserDto adminDto = new UserDto(adminId, "admin", "Admin", "1999-01-01");
        AccountDto accountDto = new AccountDto(userId, userDto, BigDecimal.TEN,  List.of(adminDto));
        when(accountService.getAccount(any(String.class))).thenReturn(Mono.just(accountDto));

        StepVerifier.create(accountController.getAccount(createToken(userId)))
                .consumeNextWith(actualAccountDto -> {
                    assertThat(actualAccountDto.id()).isEqualTo(accountDto.id());
                    assertThat(actualAccountDto.user()).isEqualTo(accountDto.user());
                }).verifyComplete();
    }

    @Test
    void changeCashIsUnauthorizedTest() {
        OperationResultDto operationResultDto = new OperationResultDto(new UUID(0,0), "user", false, "error message");
        when(accountService.changeCash(any(String.class), any(BigDecimal.class), any(CashAction.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(accountController.changeCash(new ChangeCashRequestDto("user", BigDecimal.TEN, CashAction.PUT)))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    @Test
    void changeCashTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(accountService.changeCash(any(String.class), any(BigDecimal.class), any(CashAction.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(accountController.changeCash(new ChangeCashRequestDto("user", BigDecimal.TEN, CashAction.PUT)))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    @Test
    void transferCashIsUnauthorizedTest() {
        OperationResultDto operationResultDto = new OperationResultDto(new UUID(0,0), "user", false, "error message");
        when(accountService.transferCash(any(String.class), any(BigDecimal.class), any(String.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(accountController.transferCash(new TransferCashRequestDto("user", BigDecimal.TEN, "admin")))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    @Test
    void transferCashTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(accountService.transferCash(any(String.class), any(BigDecimal.class), any(String.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(accountController.transferCash(new TransferCashRequestDto("user", BigDecimal.TEN, "admin")))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    @Test
    void changeUserDataIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(new UUID(0,0), "user", false, "error message");
        when(userService.changeUserData(any(String.class), any(String.class), any(LocalDate.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(userController.changeUserData(new ChangeUserDataRequestDto("user", "User", LocalDate.of(1999, 1, 1)), createToken(userId)))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    @Test
    void changeUserDataTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(userService.changeUserData(any(String.class), any(String.class), any(LocalDate.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(userController.changeUserData(new ChangeUserDataRequestDto("user", "User", LocalDate.of(1999, 1, 1)), createToken(userId)))
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
