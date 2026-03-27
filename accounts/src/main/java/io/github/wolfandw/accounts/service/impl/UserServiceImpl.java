package io.github.wolfandw.accounts.service.impl;

import io.github.wolfandw.accounts.model.User;
import io.github.wolfandw.accounts.repository.UserRepository;
import io.github.wolfandw.accounts.service.UserService;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Реализация {@link UserService}.
 */
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
    public UserServiceImpl(UserRepository userRepository, OutboxRepository outboxRepository) {
        this.userRepository = userRepository;
        this.outboxRepository = outboxRepository;
    }

    @Override
    public Mono<OperationResultDto> changeUserData(String login, String name, LocalDate birthdate) {
        LOG.info(createMessage(login, "Accounts. Обработка запроса на изменение данных пользователя"));
        return userRepository.findByLogin(login).flatMap(user -> {
            if (LocalDate.now().getYear() - birthdate.getYear() < 18) {
                return Mono.just(new OperationResultDto(false, "Accounts. Недостаточно полных лет (18+)", null));
            }
            return userRepository.save(changeUser(user, name, birthdate)).
                flatMap(changedUser -> outboxRepository.save(createOutbox(login, "Accounts. Изменены данные пользователя"))).
                    map(createdOutbox -> new OperationResultDto(true, null, createdOutbox.getMessage()));
                }).switchIfEmpty(Mono.defer(() -> Mono.just(new OperationResultDto(false, "Accounts. Не удалось изменить данные пользователя", null))));
    }

    private User changeUser(User user, String name, LocalDate birthdate) {
        user.setName(name);
        user.setBirthdate(birthdate);
        return user;
    }

    private Outbox createOutbox(String login, String message) {
        Outbox outbox = new Outbox();
        outbox.setMessage(createMessage(login, message));
        return outbox;
    }

    private String createMessage(String login, String message) {
        return message + ": '" + login + "'";
    }
}
