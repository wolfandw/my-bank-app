package io.github.wolfandw.accounts.itest.controller;

import com.ibm.icu.math.BigDecimal;
import io.github.wolfandw.accounts.AccountsApplication;
import io.github.wolfandw.accounts.repository.AccountRepository;
import io.github.wolfandw.accounts.repository.UserRepository;
import io.github.wolfandw.accounts.service.AccountsService;
import io.github.wolfandw.accounts.service.UserService;
import io.github.wolfandw.chassis.dto.AccountDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.dto.UserDto;
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

import java.time.LocalDate;
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
@ActiveProfiles("test")
@SpringBootTest(classes = AccountsApplication.class,
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
public class AccountsControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AccountsService accountsService;

    @MockitoBean
    private OutboxRepository outboxRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @MockitoBean
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

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
            .expectStatus().isFound();
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
            .expectStatus().isFound();
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
                .expectStatus().isFound();
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

    @Test
    void changeUserDataIsUnauthorizedTest() {
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
                .expectStatus().isFound();
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
