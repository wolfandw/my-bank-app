package io.github.wolfandw.accounts.repository;

import io.github.wolfandw.accounts.model.Account;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Репозиторий счетов.
 */
public interface AccountRepository extends R2dbcRepository<Account, UUID> {
    /**
     * Возвращает счет пользователя по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return счет пользователя
     */
    Mono<Account> findByUserId(UUID userId);
}
