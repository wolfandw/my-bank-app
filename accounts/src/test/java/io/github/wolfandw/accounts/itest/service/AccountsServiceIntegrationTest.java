package io.github.wolfandw.accounts.itest.service;

import io.github.wolfandw.accounts.itest.BaseAccountsIntegrationTest;
import io.github.wolfandw.chassis.dto.AccountDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Интеграционный тест сервиса счетов.
 */
public class AccountsServiceIntegrationTest extends BaseAccountsIntegrationTest {
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
    void getAccountIsUnauthorizedTest() {
        trxStepVerifier.create(accountsService.getAccount("user"))
                .verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = {"USER", "CASH_WRITE"})
    void getAccountTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID adminId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        UserDto userDto = new UserDto(userId, "user", "User", "1999-01-01");
        UserDto adminDto = new UserDto(adminId, "admin", "Admin", "1999-01-01");
        AccountDto accountDto = new AccountDto(userId, userDto, BigDecimal.TEN,  List.of(adminDto));

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AccountDto.class))
                .thenReturn(Mono.just(accountDto));

        trxStepVerifier.create(accountsService.getAccount("user")).
                consumeNextWith(actualAccountDto -> {
                    assertThat(actualAccountDto.id()).isEqualTo(UUID.fromString("650e8400-e29b-41d4-a716-446655440000"));
                    assertThat(actualAccountDto.user()).isEqualTo(accountDto.user());
                }).verifyComplete();
    }

    @Test
    void changeCashIsUnauthorizedTest() {
        trxStepVerifier.create(accountsService.changeCash("user", BigDecimal.TEN, CashAction.PUT))
                .verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = {"CASH_WRITE", "ACCOUNTS_SERVICE_CLIENT"})
    void changeCashTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OperationResultDto.class))
                .thenReturn(Mono.just(operationResultDto));

        trxStepVerifier.create(accountsService.changeCash("user", BigDecimal.TEN, CashAction.PUT)).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.accepted()).isTrue();
                }).verifyComplete();
    }

    @Test
    void transferCashIsUnauthorizedTest() {
        trxStepVerifier.create(accountsService.transferCash("user", BigDecimal.TEN, "admin"))
                .verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = {"TRANSFER_WRITE", "ACCOUNTS_SERVICE_CLIENT"})
    void transferCashTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OperationResultDto.class))
                .thenReturn(Mono.just(operationResultDto));

        trxStepVerifier.create(accountsService.transferCash("user", BigDecimal.TEN, "admin")).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.accepted()).isTrue();
                }).verifyComplete();
    }

    @Test
    void changeUserDataIsUnauthorizedTest() {
        trxStepVerifier.create(userService.changeUserData("user","User", LocalDate.of(1999, 1, 1)))
                .verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = {"USER", "ACCOUNTS_WRITE"})
    void changeUserDataTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OperationResultDto.class))
                .thenReturn(Mono.just(operationResultDto));

        trxStepVerifier.create(userService.changeUserData("user","User", LocalDate.of(1999, 1, 1))).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.accepted()).isTrue();
                }).verifyComplete();
    }
}
