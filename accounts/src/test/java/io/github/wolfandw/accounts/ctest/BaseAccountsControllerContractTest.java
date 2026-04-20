package io.github.wolfandw.accounts.ctest;

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
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
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
 * Базовый контрактный тест поставщика сервиса счетов.
 */
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
@ActiveProfiles("contract-test")
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseAccountsControllerContractTest {
    @MockitoBean
    private AccountsService cashService;

    @MockitoBean
    private UserService userService;

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

    @Autowired
    protected WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID adminId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");

        UserDto user = new UserDto(userId, "user", "User", "1999-01-01");
        UserDto admin = new UserDto(adminId, "admin", "Admin", "1999-01-01");
        AccountDto accountDto = new AccountDto(userId, user, BigDecimal.TEN,  List.of(admin));

        when(cashService.getAccount(any(String.class))).thenReturn(Mono.just(accountDto));

        when(cashService.changeCash(any(String.class), any(BigDecimal.class), any(CashAction.class)))
                .thenReturn(Mono.just(operationResultDto));
        when(cashService.transferCash(any(String.class), any(BigDecimal.class), any(String.class)))
                .thenReturn(Mono.just(operationResultDto));

        when(userService.changeUserData(any(String.class), any(String.class), any(LocalDate.class)))
                .thenReturn(Mono.just(operationResultDto));

        RestAssuredWebTestClient.webTestClient(webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"),
                                new SimpleGrantedAuthority("ROLE_CASH_WRITE"),
                                new SimpleGrantedAuthority("ROLE_TRANSFER_WRITE"),
                                new SimpleGrantedAuthority("ROLE_ACCOUNTS_WRITE"),
                                new SimpleGrantedAuthority("ROLE_ACCOUNTS_SERVICE_CLIENT"))
                        .jwt(jwt -> jwt
                                .claim("preferred_username", "user")
                                .subject(userId.toString())))
        );
    }
}
