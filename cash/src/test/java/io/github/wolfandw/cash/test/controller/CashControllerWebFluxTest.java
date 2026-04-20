package io.github.wolfandw.cash.test.controller;

import io.github.wolfandw.cash.controller.CashController;
import io.github.wolfandw.cash.service.CashService;
import io.github.wolfandw.chassis.dto.CashAction;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

/**
 * Модульные тесты контроллера наличности.
 */
@WebFluxTest(CashController.class)
public class CashControllerWebFluxTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean(reset = MockReset.BEFORE)
    private CashService cashService;

    @MockitoBean(reset = MockReset.BEFORE)
    private OutboxRepository outboxRepository;

    @Test
    void transferIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", false, "error message");
        when(cashService.changeCash(any(String.class), any(BigDecimal.class), any(CashAction.class)))
                .thenReturn(Mono.just(operationResultDto));

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/cash")
                        .queryParam("login", "user")
                        .queryParam("value", BigDecimal.TEN)
                        .queryParam("action", CashAction.PUT)
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void transferNotificationTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(cashService.changeCash(any(String.class), any(BigDecimal.class), any(CashAction.class)))
                .thenReturn(Mono.just(operationResultDto));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_CASH_WRITE"))
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
}
