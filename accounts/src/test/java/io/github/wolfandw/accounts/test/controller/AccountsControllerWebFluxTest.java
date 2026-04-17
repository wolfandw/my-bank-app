package io.github.wolfandw.accounts.test.controller;

import com.ibm.icu.math.BigDecimal;
import io.github.wolfandw.accounts.controller.AccountsController;
import io.github.wolfandw.accounts.repository.AccountRepository;
import io.github.wolfandw.accounts.repository.UserRepository;
import io.github.wolfandw.accounts.service.AccountsService;
import io.github.wolfandw.chassis.dto.AccountDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.dto.UserDto;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

/**
 * Модульные тесты контроллера счетов.
 */
@WebFluxTest(AccountsController.class)
public class AccountsControllerWebFluxTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean(reset = MockReset.BEFORE)
    private AccountsService accountsService;

    @MockitoBean(reset = MockReset.BEFORE)
    private OutboxRepository outboxRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    private UserRepository userRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    private AccountRepository accountRepository;

        @Test
    void getAccountIsUnauthorizedTest() {
            UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            UUID adminId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
            UserDto userDto = new UserDto(userId, "user", "User", "1999-01-01");
            UserDto adminDto = new UserDto(adminId, "admin", "Admin", "1999-01-01");
            AccountDto accountDto = new AccountDto(userId, userDto, java.math.BigDecimal.TEN,  List.of(adminDto));
            when(accountsService.getAccount(any(String.class))).thenReturn(Mono.just(accountDto));

        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder
                    .path("/api/account")
                    .build())
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void getAccountTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID adminId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        UserDto userDto = new UserDto(userId, "user", "User", "1999-01-01");
        UserDto adminDto = new UserDto(adminId, "admin", "Admin", "1999-01-01");
        AccountDto accountDto = new AccountDto(userId, userDto, java.math.BigDecimal.TEN,  List.of(adminDto));
        when(accountsService.getAccount(any(String.class))).thenReturn(Mono.just(accountDto));

        webTestClient
            .mutateWith(mockJwt()
                    .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                    .jwt(jwt -> jwt
                            .claim("preferred_username", "user")
                            .subject(userId.toString())))
            .get()
            .uri(uriBuilder -> uriBuilder
                    .path("/api/account")
                    .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(AccountDto.class)
            .value(actualAccountDto -> {
                assertThat(actualAccountDto.id()).isEqualTo(accountDto.id());
                assertThat(actualAccountDto.user()).isEqualTo(accountDto.user());
            });
    }

    @Test
    void changeCashIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(accountsService.changeCash(any(String.class), any(java.math.BigDecimal.class), any(CashAction.class)))
                .thenReturn(Mono.just(operationResultDto));

        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(uriBuilder -> uriBuilder
                    .path("/api/cash")
                    .queryParam("login", "user")
                    .queryParam("value", BigDecimal.TEN)
                    .queryParam("action",CashAction.PUT)
                    .build())
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void changeCashTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(accountsService.changeCash(any(String.class), any(java.math.BigDecimal.class), any(CashAction.class)))
            .thenReturn(Mono.just(operationResultDto));

        webTestClient
            .mutateWith(csrf())
            .mutateWith(mockJwt()
                    .authorities(new SimpleGrantedAuthority("ROLE_CASH_WRITE"), new SimpleGrantedAuthority("ROLE_ACCOUNTS_SERVICE_CLIENT"))
                    .jwt(jwt -> jwt
                            .claim("preferred_username", "user")
                            .subject(userId.toString())))
            .post()
            .uri(uriBuilder -> uriBuilder
                    .path("/api/cash")
                    .queryParam("login", "user")
                    .queryParam("value", BigDecimal.TEN)
                    .queryParam("action", CashAction.PUT)
                    .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(OperationResultDto.class)
            .value(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
            });
    }

    @Test
    void transferIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(accountsService.transferCash(any(String.class), any(java.math.BigDecimal.class), any(String.class)))
            .thenReturn(Mono.just(operationResultDto));

        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(uriBuilder -> uriBuilder
                    .path("/api/transfer")
                    .queryParam("login", "user")
                    .queryParam("value", BigDecimal.TEN)
                    .queryParam("recipient","admin")
                    .build())
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void transferCashTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(accountsService.transferCash(any(String.class), any(java.math.BigDecimal.class), any(String.class)))
            .thenReturn(Mono.just(operationResultDto));

        webTestClient
            .mutateWith(csrf())
            .mutateWith(mockJwt()
                    .authorities(new SimpleGrantedAuthority("ROLE_TRANSFER_WRITE"), new SimpleGrantedAuthority("ROLE_ACCOUNTS_SERVICE_CLIENT"))
                    .jwt(jwt -> jwt
                            .claim("preferred_username", "user")
                            .subject(userId.toString())))
            .post()
            .uri(uriBuilder -> uriBuilder
                    .path("/api/transfer")
                    .queryParam("login", "user")
                    .queryParam("value", BigDecimal.TEN)
                    .queryParam("recipient","admin")
                    .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(OperationResultDto.class)
            .value(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
            });
    }
}
