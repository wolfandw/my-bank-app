package io.github.wolfandw.accounts.test.controller;

import io.github.wolfandw.accounts.controller.UserController;
import io.github.wolfandw.accounts.repository.AccountRepository;
import io.github.wolfandw.accounts.repository.UserRepository;
import io.github.wolfandw.accounts.service.UserService;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

/**
 * Модульные тесты контроллера пользователей.
 */
@WebFluxTest(UserController.class)
public class UserControllerWebFluxTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean(reset = MockReset.BEFORE)
    private UserService userService;

    @MockitoBean(reset = MockReset.BEFORE)
    private OutboxRepository outboxRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    private UserRepository userRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    private AccountRepository accountRepository;

    @Test
    void changeUserDataIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", false, "error message");
        when(userService.changeUserData(any(String.class), any(String.class), any(LocalDate.class)))
                .thenReturn(Mono.just(operationResultDto));

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/account")
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
        when(userService.changeUserData(any(String.class), any(String.class), any(LocalDate.class)))
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
                        .path("/api/account")
                        .queryParam("login", "user")
                        .queryParam("name", "User")
                        .queryParam("birthdate", LocalDate.of(1999, 1, 1))
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
