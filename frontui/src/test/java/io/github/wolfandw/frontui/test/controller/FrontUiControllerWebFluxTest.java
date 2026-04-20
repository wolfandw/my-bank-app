package io.github.wolfandw.frontui.test.controller;

import io.github.wolfandw.chassis.dto.AccountDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.dto.UserDto;
import io.github.wolfandw.frontui.controller.FrontUiController;
import io.github.wolfandw.frontui.service.FrontUiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

/**
 * Модульные тесты контроллера ui.
 */
@WebFluxTest(FrontUiController.class)
public class FrontUiControllerWebFluxTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean(reset = MockReset.BEFORE)
    private FrontUiService frontUiService;

    @Test
    void getAccountIsUnauthorizedTest() {
            UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            UUID adminId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
            UserDto userDto = new UserDto(userId, "user", "User", "1999-01-01");
            UserDto adminDto = new UserDto(adminId, "admin", "Admin", "1999-01-01");
            AccountDto accountDto = new AccountDto(userId, userDto, java.math.BigDecimal.TEN,  List.of(adminDto));
            when(frontUiService.getAccount()).thenReturn(Mono.just(accountDto));

        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder
                    .path("/account")
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
        when(frontUiService.getAccount()).thenReturn(Mono.just(accountDto));

        webTestClient
            .mutateWith(mockJwt()
                    .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                    .jwt(jwt -> jwt
                            .claim("preferred_username", "user")
                            .subject(userId.toString())))
            .get()
            .uri(uriBuilder -> uriBuilder
                    .path("/account")
                    .build())
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void changeCashIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(frontUiService.changeCash(any(java.math.BigDecimal.class), any(CashAction.class)))
                .thenReturn(Mono.just(operationResultDto));

        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(uriBuilder -> uriBuilder
                    .path("/cash")
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
        when(frontUiService.changeCash(any(java.math.BigDecimal.class), any(CashAction.class)))
            .thenReturn(Mono.just(operationResultDto));

        webTestClient
            .mutateWith(csrf())
            .mutateWith(mockJwt()
                    .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                    .jwt(jwt -> jwt
                            .claim("preferred_username", "user")
                            .subject(userId.toString())))
            .post()
            .uri(uriBuilder -> uriBuilder
                    .path("/cash")
                    .queryParam("login", "user")
                    .queryParam("value", BigDecimal.TEN)
                    .queryParam("action", CashAction.PUT)
                    .build())
            .exchange()
            .expectStatus().is3xxRedirection();
    }

    @Test
    void transferIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(frontUiService.transferCash(any(java.math.BigDecimal.class), any(String.class)))
            .thenReturn(Mono.just(operationResultDto));

        webTestClient
            .mutateWith(csrf())
            .post()
            .uri(uriBuilder -> uriBuilder
                    .path("/transfer")
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
        when(frontUiService.transferCash(any(java.math.BigDecimal.class), any(String.class)))
            .thenReturn(Mono.just(operationResultDto));

        webTestClient
            .mutateWith(csrf())
            .mutateWith(mockJwt()
                    .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                    .jwt(jwt -> jwt
                            .claim("preferred_username", "user")
                            .subject(userId.toString())))
            .post()
            .uri(uriBuilder -> uriBuilder
                    .path("/transfer")
                    .queryParam("login", "user")
                    .queryParam("value", BigDecimal.TEN)
                    .queryParam("recipient","admin")
                    .build())
            .exchange()
            .expectStatus().is3xxRedirection();
    }

    @Test
    void changeUserDataIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", false, "error message");
        when(frontUiService.changeUserData(any(String.class), any(LocalDate.class)))
                .thenReturn(Mono.just(operationResultDto));

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/account")
                        .queryParam("login", "user")
                        .queryParam("name", "User")
                        .queryParam("birthdate", LocalDate.of(1999, 1, 1))
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void changeUserDataTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(frontUiService.changeUserData(any(String.class), any(LocalDate.class)))
                .thenReturn(Mono.just(operationResultDto));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ACCOUNTS_WRITE"))
                        .jwt(jwt -> jwt
                                .claim("preferred_username", "user")
                                .subject(userId.toString())))
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/account")
                        .queryParam("login", "user")
                        .queryParam("name", "User")
                        .queryParam("birthdate", LocalDate.of(1999, 1, 1))
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection();
    }
}
