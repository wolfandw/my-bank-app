package io.github.wolfandw.accounts.test.controller;

import io.github.wolfandw.accounts.controller.UserController;
import io.github.wolfandw.accounts.service.UserService;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.ChangeCashRequestDto;
import io.github.wolfandw.chassis.dto.ChangeUserDataRequestDto;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульные тесты контроллера пользователей.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void changeUserDataIsUnauthorizedTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(new UUID(0,0), "user", false, "error message");
        when(userService.changeUserData(any(String.class), any(String.class), any(LocalDate.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(userController.changeUserData(new ChangeUserDataRequestDto("user", "User", LocalDate.of(1999, 1, 1)), createToken(userId)))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    @Test
    void changeUserDataTest() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");
        when(userService.changeUserData(any(String.class), any(String.class), any(LocalDate.class)))
                .thenReturn(Mono.just(operationResultDto));

        StepVerifier.create(userController.changeUserData(new ChangeUserDataRequestDto("user", "User", LocalDate.of(1999, 1, 1)), createToken(userId)))
                .consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    private Jwt createToken(UUID userId) {
        return Jwt.withTokenValue("custom-token")
                .header("alg", "none")
                .header("sub", userId.toString())
                .claim("preferred_username", "user")
                .build();
    }
}
