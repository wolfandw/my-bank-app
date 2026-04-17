package io.github.wolfandw.accounts.test.service;

import io.github.wolfandw.accounts.model.Account;
import io.github.wolfandw.accounts.model.User;
import io.github.wolfandw.accounts.repository.AccountRepository;
import io.github.wolfandw.accounts.repository.UserRepository;
import io.github.wolfandw.accounts.service.impl.AccountsServiceImpl;
import io.github.wolfandw.chassis.dto.AccountDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.dto.UserDto;
import io.github.wolfandw.chassis.model.Outbox;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульный тест сервиса счетов.
 */
@ExtendWith(MockitoExtension.class)
public class AccountsServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountsServiceImpl accountService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    @Test
    void getAccountTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Outbox outbox = new Outbox();
        outbox.setId(outboxId);
        outbox.setUserId(outboxId);
        outbox.setMessage("test message");

        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        User user = new User();
        user.setId(userId);
        user.setName("User");
        user.setBirthdate(LocalDate.of(1999, 1, 1));
        user.setLogin("user");

        UUID adminId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        User admin = new User();
        admin.setId(adminId);
        admin.setName("Admin");
        admin.setBirthdate(LocalDate.of(1999, 1, 1));
        admin.setLogin("admin");

        when(userRepository.findByLogin(any(String.class)))
                .thenReturn(Mono.just(user));
        when(userRepository.findAllByLoginNot(any(String.class)))
                .thenReturn(Flux.just(admin));

        Account account = new Account();
        account.setId(outboxId);
        account.setUserId(outboxId);
        account.setBalance(BigDecimal.TEN);
        when(accountRepository.findByUserId(any(UUID.class)))
                .thenReturn(Mono.just(account));


        OperationResultDto operationResultDto = new OperationResultDto(userId, "user", true, "test message");

        UserDto userDto = new UserDto(userId, "user", "User", "1999-01-01");
        UserDto adminDto = new UserDto(adminId, "admin", "Admin", "1999-01-01");
        AccountDto accountDto = new AccountDto(userId, userDto, BigDecimal.TEN,  List.of(adminDto));

        StepVerifier.create(accountService.getAccount("user")).
                consumeNextWith(actualAccountDto -> {
                    assertThat(actualAccountDto.id()).isEqualTo(accountDto.id());
                    assertThat(actualAccountDto.user()).isEqualTo(accountDto.user());
                }).verifyComplete();
    }

    @Test
    void changeCashTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        User user = new User();
        user.setId(outboxId);
        user.setBirthdate(LocalDate.of(1999, 1, 1));
        user.setLogin("user");
        when(userRepository.findByLogin(any(String.class)))
                .thenReturn(Mono.just(user));

        Account account = new Account();
        account.setId(outboxId);
        account.setUserId(outboxId);
        account.setBalance(BigDecimal.TEN);
        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(account));
        when(accountRepository.findByUserId(any(UUID.class)))
                .thenReturn(Mono.just(account));

        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");
        StepVerifier.create(accountService.changeCash("user", BigDecimal.TEN, CashAction.PUT)).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    AssertionsForClassTypes.assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }

    @Test
    void transferCashTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        User user = new User();
        user.setId(outboxId);
        user.setBirthdate(LocalDate.of(1999, 1, 1));
        user.setLogin("user");
        when(userRepository.findByLogin(any(String.class)))
                .thenReturn(Mono.just(user));

        Account account = new Account();
        account.setId(outboxId);
        account.setUserId(outboxId);
        account.setBalance(BigDecimal.TEN);
        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(account));
        when(accountRepository.findByUserId(any(UUID.class)))
                .thenReturn(Mono.just(account));

        OperationResultDto operationResultDto = new OperationResultDto(outboxId, "user", true, "test message");
        StepVerifier.create(accountService.transferCash("user", BigDecimal.TEN, "admin")).
                consumeNextWith(actualResult -> {
                    assertThat(actualResult.userId()).isEqualTo(operationResultDto.userId());
                    AssertionsForClassTypes.assertThat(actualResult.accepted()).isEqualTo(operationResultDto.accepted());
                }).verifyComplete();
    }
}
