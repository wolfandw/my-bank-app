package io.github.wolfandw.accounts.test.service;

import io.github.wolfandw.accounts.model.User;
import io.github.wolfandw.accounts.repository.UserRepository;
import io.github.wolfandw.accounts.service.impl.UserServiceImpl;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульный тест сервиса пользователей.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    @Test
    void changeUserDataTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Outbox outbox = new Outbox();
        outbox.setId(outboxId);
        outbox.setUserId(outboxId);
        outbox.setMessage("test message");
        when(outboxRepository.save(any(Outbox.class)))
                .thenReturn(Mono.just(outbox));

        User user = new User();
        user.setId(outboxId);
        user.setBirthdate(LocalDate.of(1999, 1, 1));
        user.setLogin("user");
        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.just(user));
        when(userRepository.findByLogin(any(String.class)))
                .thenReturn(Mono.just(user));

        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");
        StepVerifier.create(userService.changeUserData("user","User", LocalDate.of(1999, 1, 1))).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    AssertionsForClassTypes.assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }
}
