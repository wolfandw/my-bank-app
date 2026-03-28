package io.github.wolfandw.accounts.service;

import io.github.wolfandw.accounts.model.User;
import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Сервис для работы с пользователями.
 */
public interface UserService {
    /**
     * Изменяет имя и дату рождения пользователя.
     *
     * @param login логин пользователя
     * @param name имя
     * @param birthdate дата рождения
     * @return DTO-модель результата операции
     */
    Mono<OperationResultDto> changeUserData(String login, String name, LocalDate birthdate);
}
