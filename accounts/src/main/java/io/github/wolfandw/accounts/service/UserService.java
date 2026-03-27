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
//    /**
//     * Возвращает DTO-представление пользователя по указанному логину.
//     * Если пользователь с таким логином отсутствует - он будет создан.
//     *
//     * @param login логин пользователя
//     * @return DTO-представление пользователя
//     */
//    Mono<UserDto> getOrCreateUser(String login);
//
//    /**
//     * Возвращает DTO-представление пользователя по указанному логину.
//     *
//     * @param login логин пользователя
//     * @return DTO-представление пользователя
//     */
//    Mono<UserDto> getUser(String login);
//
//    /**
//     * Возвращает DTO-представление пользователя по указанному логину.
//     *
//     * @param login логин пользователя
//     * @return DTO-представление пользователя
//     */
//    Flux<UserDto> getRecipients(String login);

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
