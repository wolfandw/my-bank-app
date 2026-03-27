package io.github.wolfandw.accounts.service;

import io.github.wolfandw.chassis.dto.AccountDto;
import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Сервис для работы с аккаунтами.
 */
public interface AccountsService {
    /**
     * Возвращает DTO-представление счета.
     *
     * @param login логин пользователя
     * @return DTO-представление счета
     */
    Mono<AccountDto> getAccount(String login);

    /**
     * Изменить остаток наличности.
     *
     * @param login логин пользователя
     * @param value сумма изменения
     * @param action действие (снять, положить)
     * @return DTO-модель результата операции
     */
    Mono<OperationResultDto> changeCash(String login, BigDecimal value, CashAction action);

    /**
     * Перевести наличные.
     *
     * @param login логин пользователя
     * @param value сумма перевода
     * @param recipient получатель перевода
     * @return DTO-модель результата операции
     */
    Mono<OperationResultDto> transferCash(String login, BigDecimal value, String recipient);
}
