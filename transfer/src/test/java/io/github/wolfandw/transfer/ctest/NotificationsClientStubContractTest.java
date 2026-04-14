package io.github.wolfandw.transfer.ctest;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.chassis.service.OutboxProcessorService;
import io.github.wolfandw.chassis.service.OutboxSchedulerService;
import io.github.wolfandw.transfer.TransferApplication;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("contract-test")
@SpringBootTest(classes = TransferApplication.class,
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
    @Autowired
    private OutboxProcessorService outboxProcessorService;

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

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void processSendingUnsentOutboxTest() {
        Outbox o0False = new Outbox();
        o0False.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        o0False.setUserId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        o0False.setMessage("Тестовое сообщение для отправки");
        o0False.setSent(false);

        Outbox o0True = new Outbox();
        o0True.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        o0True.setUserId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        o0True.setMessage("test0");
        o0True.setSent(true);

        Flux<Outbox> outboxFlux = Flux.just(o0False);
        when(outboxRepository.findAllBySent(any(Boolean.class))).thenReturn(outboxFlux);
        when(outboxRepository.findById(any(UUID.class))).thenReturn(Mono.just(o0False));
        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(o0True));
        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(o0True));

        webTestClient
            .post()
            .uri(uriBuilder -> buildUri(uriBuilder, o0False.getId(), o0False.getUserId(), o0False.getMessage()))
            .header("Authorization", "Bearer any-token")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .value(actualOutboxId -> {
                assertThat(actualOutboxId).isEqualTo(o0False.getId().toString());
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
