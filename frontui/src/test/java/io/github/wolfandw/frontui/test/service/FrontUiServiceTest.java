package io.github.wolfandw.frontui.test.service;

import io.github.wolfandw.chassis.dto.AccountDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.dto.UserDto;
import io.github.wolfandw.frontui.service.impl.FrontUiServiceImpl;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Модульный тест сервиса ui.
 */
@ExtendWith(MockitoExtension.class)
public class FrontUiServiceTest {
    @InjectMocks
    private FrontUiServiceImpl frontUiService;

    @Mock
    private Tracer tracer;

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.RequestHeadersUriSpec  requestHeadersUriSpec;

    @Test
    void getAccountTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID adminId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

        UserDto userDto = new UserDto(userId, "user", "User", "1999-01-01");
        UserDto adminDto = new UserDto(adminId, "admin", "Admin", "1999-01-01");
        AccountDto accountDto = new AccountDto(userId, userDto, BigDecimal.TEN,  List.of(adminDto));

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(any(String.class), any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AccountDto.class))
                .thenReturn(Mono.just(accountDto));

        StepVerifier.create(frontUiService.getAccount()).
                consumeNextWith(actualAccountDto -> {
                    assertThat(actualAccountDto.id()).isEqualTo(accountDto.id());
                    assertThat(actualAccountDto.user()).isEqualTo(accountDto.user());
                }).verifyComplete();
    }

    @Test
    void changeCashTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(String.class), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OperationResultDto.class))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(frontUiService.changeCash(BigDecimal.TEN, CashAction.PUT)).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    @Test
    void transferCashTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(String.class), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OperationResultDto.class))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(frontUiService.transferCash(BigDecimal.TEN, "admin")).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    @Test
    void changeUserDataTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(String.class), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OperationResultDto.class))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(frontUiService.changeUserData("User", LocalDate.of(1999, 1, 1))).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }
}
