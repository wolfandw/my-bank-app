package io.github.wolfandw.accounts.service.impl;

import io.github.wolfandw.accounts.model.Account;
import io.github.wolfandw.accounts.model.User;
import io.github.wolfandw.accounts.repository.AccountRepository;
import io.github.wolfandw.accounts.repository.UserRepository;
import io.github.wolfandw.accounts.service.AccountsService;
import io.github.wolfandw.chassis.dto.*;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Реализация {@link AccountsService}
 */
@Service
public class AccountsServiceImpl implements AccountsService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountsServiceImpl.class);

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;

    /**
     * Создает сервис счетов.
     *
     * @param accountRepository репозиторий счетов
     * @param userRepository    репозиторий пользователей
     * @param outboxRepository  репозиторий сообщений
     */
    public AccountsServiceImpl(AccountRepository accountRepository,
                               UserRepository userRepository,
                               OutboxRepository outboxRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    public Mono<AccountDto> getAccount(String login) {
        LOG.info(createMessage(login, "Accounts. Обработка запроса на получение данных счета"));
        Mono<AccountDto> accountDtoMono = getOrCreateUser(login).flatMap(user -> getOrCreateAccount(user).
                map(account -> new AccountDto(account.getId(), mapToUserDto(user), account.getBalance(), new ArrayList<>())));
        Mono<List<UserDto>> userDtoListMono = userRepository.findAllByLoginNot(login).map(this::mapToUserDto).collectList();
        return accountDtoMono.zipWith(userDtoListMono).map(tuple -> {
            AccountDto accountDto = tuple.getT1();
            List<UserDto> userDtoList = tuple.getT2();
            accountDto.users().addAll(userDtoList);
            return accountDto;
        }).map(acc -> {
            LOG.info(String.valueOf(acc.id()));
            return acc;
        })
                .onErrorResume(e -> {
                    LOG.error(e.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    @Transactional
    public Mono<OperationResultDto> changeCash(String login, BigDecimal value, CashAction action) {
        LOG.info(createMessage(login, "Accounts. Обработка запроса на изменение состояния счета"));
        return userRepository.findByLogin(login).flatMap(user -> accountRepository.findByUserId(user.getId()).flatMap(
                account -> {
                    BigDecimal currentBalance = account.getBalance();
                    if (action == CashAction.GET) {
                        if (currentBalance.compareTo(value) < 0) {
                            return Mono.just(new OperationResultDto(user.getId(),
                                    login,
                                    false,
                                    "Accounts. Недостаточно средств на счету"));
                        } else {
                            account.setBalance(currentBalance.subtract(value));
                            return accountRepository.save(account).
                                    map(changedAccount -> new OperationResultDto(user.getId(),
                                            login,
                                            true,
                                            "Accounts. Снято %s руб".formatted(value.toPlainString())));
                        }
                    } else {
                        account.setBalance(currentBalance.add(value));
                        return accountRepository.save(account).
                                map(changedAccount -> new OperationResultDto(user.getId(),
                                        login,
                                        true,
                                        "Accounts. Внесено %s руб".formatted(value.toPlainString())));
                    }
                }
        )).switchIfEmpty(Mono.defer(() -> Mono.just(new OperationResultDto(new UUID(0, 0), login, false, "Accounts. Не удалось изменить состояние счета"))));
    }

    @Override
    @Transactional
    public Mono<OperationResultDto> transferCash(String login, BigDecimal value, String recipient) {
        LOG.info(createMessage(login, "Accounts. Обработка запроса на перевод денежных средств со счета"));
        return userRepository.findByLogin(login).flatMap(user -> accountRepository.findByUserId(user.getId()).flatMap(
                account -> {
                    BigDecimal currentBalance = account.getBalance();
                    if (currentBalance.compareTo(value) < 0) {
                        return Mono.just(new OperationResultDto(user.getId(), login, false, "Accounts. Недостаточно средств на счету"));
                    }
                    account.setBalance(currentBalance.subtract(value));
                    return userRepository.findByLogin(recipient).flatMap(userRecipient -> accountRepository.findByUserId(userRecipient.getId()).flatMap(
                            recipientAccount -> {
                                BigDecimal recipientBalance = account.getBalance();
                                recipientAccount.setBalance(recipientBalance.add(value));
                                return accountRepository.save(recipientAccount).
                                        flatMap(changedRecipientAccount -> accountRepository.save(account)).
                                        map(createdOutbox -> new OperationResultDto(user.getId(),
                                                login,
                                                true,
                                                "Успешно переведено %s руб клиенту %s".formatted(value.toPlainString(), recipient)));
                            }));
                }
        )).switchIfEmpty(Mono.defer(() -> Mono.just(new OperationResultDto(new UUID(0, 0), login, false, "Accounts. Не выполнить перевод со счета"))));
    }

    private Mono<User> getOrCreateUser(String login) {
        LOG.info(createMessage(login, "Accounts. Получение или создание пользователя"));
        return userRepository.findByLogin(login).
                switchIfEmpty(Mono.defer(() -> userRepository.save(createUser(login, login, LocalDate.of(2001, 1, 1))).
                        flatMap(createdUser -> outboxRepository.save(createOutbox(createdUser.getId(), login, "Accounts. Создан новый пользователь"))
                                .thenReturn(createdUser))));
    }

    private Mono<Account> getOrCreateAccount(User user) {
        String login = user.getLogin();
        UUID userId = user.getId();
        LOG.info(createMessage(login, "Accounts. Получение или создание счета пользователя"));
        return accountRepository.findByUserId(userId).
                switchIfEmpty(Mono.defer(() -> accountRepository.save(createAccount(userId)).
                        flatMap(createdAccount -> outboxRepository.save(createOutbox(userId,
                                        login,
                                        "Accounts. Создан новый счет пользователя"))
                                .thenReturn(createdAccount))));
    }

    private UserDto mapToUserDto(User user) {
        return new UserDto(user.getId(), user.getLogin(), user.getName(), user.getBirthdate().format(DateTimeFormatter.ISO_DATE));
    }

    private User createUser(String login, String name, LocalDate birthdate) {
        User user = new User();
        user.setLogin(login);
        user.setName(name);
        user.setBirthdate(birthdate);
        return user;
    }

    private Account createAccount(UUID userId) {
        Account account = new Account();
        account.setUserId(userId);
        return account;
    }

    private Outbox createOutbox(UUID userId, String login, String message) {
        Outbox outbox = new Outbox();
        outbox.setUserId(userId);
        outbox.setMessage(createMessage(login, message));
        return outbox;
    }

    private String createMessage(String login, String message) {
        return message + ": '" + login + "'";
    }
}
