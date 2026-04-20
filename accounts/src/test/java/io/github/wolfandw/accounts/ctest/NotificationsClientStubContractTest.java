package io.github.wolfandw.accounts.ctest;

import io.github.wolfandw.accounts.AccountsApplication;
import io.github.wolfandw.accounts.repository.AccountRepository;
import io.github.wolfandw.accounts.repository.UserRepository;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.chassis.service.OutboxSchedulerService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контрактный тест клиента сервиса счетов.
 */
@ActiveProfiles("contract-test")
@SpringBootTest(classes = AccountsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "server.port=0",
                "spring.liquibase.enabled=false",
                "spring.autoconfigure.exclude=" +
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
        ids = "io.github.wolfandw:notifications:+:stubs:8086",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@AutoConfigureWebTestClient
public class NotificationsClientStubContractTest {
    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @MockitoBean
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @MockitoBean
    private OutboxRepository outboxRepository;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private OutboxSchedulerService outboxScheduleService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void processSendingUnsentOutboxTest() {
        Outbox outbox = new Outbox();
        outbox.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        outbox.setUserId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        outbox.setMessage("Тестовое сообщение для отправки");
        outbox.setSent(false);

        webTestClient
            .post()
            .uri(uriBuilder -> buildUri(uriBuilder, outbox.getId(), outbox.getUserId(), outbox.getMessage()))
            .header("Authorization", "Bearer any-token")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .value(actualOutboxId -> {
                assertThat(actualOutboxId).isEqualTo(outbox.getId().toString());
            });
    }

    private URI buildUri(UriBuilder uriBuilder, UUID outboxId, UUID userId, String message) {
        return uriBuilder
                .scheme("http")
                .host("localhost")
                .port("8086")
                .path("/api/notifications")
                .queryParam("outboxId", outboxId)
                .queryParam("userId", userId)
                .queryParam("message", message)
                .build();
    }
}
