package io.github.wolfandw.accounts.service.impl;

import io.github.wolfandw.accounts.model.User;
import io.github.wolfandw.accounts.repository.UserRepository;
import io.github.wolfandw.accounts.service.UserService;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Реализация {@link UserService}.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;

    /**
     * Создание сервиса для работы с пользователями.
     *
     * @param userRepository репозиторий пользователей
     * @param outboxRepository репозиторий сообщений
     */
    public UserServiceImpl(UserRepository userRepository,
                           OutboxRepository outboxRepository) {
        this.userRepository = userRepository;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER') and hasRole('ACCOUNTS_WRITE')")
    public Mono<OperationResultDto> changeUserData(String login, String name, LocalDate birthdate) {
        LOG.debug(createMessage(login, "Accounts. Обработка запроса на изменение данных пользователя"));
        return userRepository.findByLogin(login).flatMap(user -> {
            if (LocalDate.now().getYear() - birthdate.getYear() < 18) {
                return Mono.just(new OperationResultDto(user.getId(),
                        login,
                        false,
                        "Accounts. Недостаточно полных лет (18+)"));
            }
            return userRepository.save(changeUser(user, name, birthdate)).
                    flatMap(changedUser -> outboxRepository.save(createOutbox(user.getId(),
                            login,
                            "Accounts. Изменены данные пользователя"))).
                    map(createdOutbox -> new OperationResultDto(user.getId(),
                            login,
                            true,
                            createdOutbox.getMessage())
                    );
        }).switchIfEmpty(Mono.defer(() -> Mono.just(new OperationResultDto(new UUID(0, 0),
                login,
                false,
                "Accounts. Не удалось изменить данные пользователя"))));
    }

    private User changeUser(User user, String name, LocalDate birthdate) {
        user.setName(name);
        user.setBirthdate(birthdate);
        return user;
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
