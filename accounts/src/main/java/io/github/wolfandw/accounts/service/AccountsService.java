package io.github.wolfandw.accounts.service;

import io.github.wolfandw.accounts.dto.AccountPageDto;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Сервис для работы с аккаунтами.
 */
public interface AccountsService {
    /**
     * Возвращает DTO-представление аккаунта.
     *
     * @return DTO-представление аккаунта
     */
    Mono<AccountPageDto> getAccount();

    /**
     * Редактирует имя и дату рождения.
     *
     * @param name имя
     * @param birthdate дата рождения
     * @return DTO-представление аккаунта
     */
    Mono<AccountPageDto> editAccount(String name, LocalDate birthdate);

}
