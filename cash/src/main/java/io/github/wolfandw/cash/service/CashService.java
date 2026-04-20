package io.github.wolfandw.cash.service;

import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Сервис для работы с наличностью.
 */
public interface CashService {
    /**
     * Изменить остаток наличности.
     *
     * @param login логин пользователя
     * @param value сумма изменения
     * @param action действие (снять, положить)
     * @return DTO-модель результата операции
     */
    Mono<OperationResultDto> changeCash(String login, BigDecimal value, CashAction action);
}
