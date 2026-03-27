package io.github.wolfandw.accounts.repository;

import io.github.wolfandw.accounts.model.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Репозиторий пользователей.
 */
public interface UserRepository  extends R2dbcRepository<User, UUID> {
    /**
     * Возвращает пользователя по логину.
     *
     * @param login логин пользователя
     * @return пользователь
     */
    Mono<User> findByLogin(String login);

    /**
     * Возвращает список пользователей без указанного.
     *
     * @param login логин пользователя
     * @return список пользователей без указанного
     */
    Flux<User> findAllByLoginNot(String login);
}
