package io.github.wolfandw.frontui.service;

import io.github.wolfandw.chassis.dto.AccountDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сервис для работы с аккаунтами.
 */
public interface FrontUiService {
    /**
     * Возвращает DTO-представление аккаунта.
     *
     * @return DTO-представление аккаунта
     */
    Mono<AccountDto> getAccount();

    /**
     * Изменяет имя и дату рождения пользователя.
     *
     * @param name имя
     * @param birthdate дата рождения
     * @return DTO-представление результата операции
     */
    Mono<OperationResultDto> changeUserData(String name, LocalDate birthdate);

    /**
     * Изменить остаток наличности.
     *
     * @param value сумма изменения
     * @param action действие (снять, положить)
     * @return DTO-представление аккаунта
     */
    Mono<OperationResultDto> changeCash(BigDecimal value, CashAction action);

    /**
     * Перевести наличные.
     *
     * @param value сумма перевода
     * @param login получатель перевода
     * @return DTO-представление аккаунта
     */
    Mono<OperationResultDto> transferCash(BigDecimal value, String login);
}
