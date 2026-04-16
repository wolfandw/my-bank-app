package io.github.wolfandw.cash.itest.controller;

import io.github.wolfandw.cash.CashApplication;
import io.github.wolfandw.cash.service.CashService;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
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
 * Модульные тесты контроллера наличных.
 */
@ActiveProfiles("test")
@SpringBootTest(classes = CashApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "server.port=0",
                "spring.liquibase.enabled=false",
                "spring.autoconfigure.exclude=" +
                    "io.github.wolfandw.chassis.configuration.OutboxProcessorAutoConfiguration," +
                    "org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration," +
                    "org.springframework.boot.health.autoconfigure.actuate.endpoint.HealthEndpointAutoConfiguration," +
                    "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration," +
                    "org.springframework.boot.jdbc.autoconfigureDataSourceAutoConfiguration," +
                    "org.springframework.boot.r2dbc.autoconfigure.R2dbcAutoConfiguration," +
                    "org.springframework.boot.data.r2dbc.autoconfigure.DataR2dbcRepositoriesAutoConfiguration",
                "spring.cloud.consul.enabled=false",
                "spring.cloud.consul.config.enabled=false",
                "spring.cloud.compatibility-verifier.enabled=false",
                "spring.main.allow-bean-definition-overriding=true"
        }
)
@AutoConfigureWebTestClient
public class CashControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CashService cashService;

    @MockitoBean
    private OutboxRepository outboxRepository;

    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @MockitoBean
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @Test
    void requestNotificationIsUnauthorizedTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
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
                .expectStatus().isFound();
    }

    @Test
    void requestNotificationTest() {
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
