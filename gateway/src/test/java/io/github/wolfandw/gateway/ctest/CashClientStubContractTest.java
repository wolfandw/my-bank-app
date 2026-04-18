package io.github.wolfandw.gateway.ctest;

import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.chassis.service.OutboxSchedulerService;
import io.github.wolfandw.gateway.GatewayApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контрактный тест клиента сервиса наличных.
 */
@ActiveProfiles("contract-test")
@SpringBootTest(classes = GatewayApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "server.port=0",
                "spring.liquibase.enabled=false",
                "spring.autoconfigure.exclude=" +
                        "io.github.wolfandw.chassis.configuration.OutboxProcessorAutoConfiguration," +
                        "io.github.wolfandw.chassis.configuration.SecurityWebFilterConfiguration," +
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
@AutoConfigureStubRunner(
        ids = "io.github.wolfandw:accounts:+:stubs:8083",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@AutoConfigureWebTestClient
public class CashClientStubContractTest {
    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @MockitoBean
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @MockitoBean
    private OutboxRepository outboxRepository;

    @MockitoBean
    private OutboxSchedulerService outboxScheduleService;

    @MockitoBean
    private SecurityWebFilterChain securityWebFilterChain;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void changeCashTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");

        webTestClient
            .post()
            .uri(uriBuilder -> buildUri(uriBuilder, "user", BigDecimal.TEN, CashAction.PUT))
            .header("Authorization", "Bearer any-token")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(OperationResultDto.class)
            .value(actualResult -> {
                assertThat(actualResult.userId()).isEqualTo(outboxId);
                assertThat(actualResult.accepted()).isTrue();
            });
    }

    private URI buildUri(UriBuilder uriBuilder, String login, BigDecimal value, CashAction action) {
        return uriBuilder
                .scheme("http")
                .host("localhost")
                .port("8083")
                .path("/api/cash")
                .queryParam("login", login)
                .queryParam("value", value)
                .queryParam("action", action)
                .build();
    }
}
