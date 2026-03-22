package io.github.wolfandw.cash.service;

import io.github.wolfandw.cash.dto.AccountPageDto;
import io.github.wolfandw.cash.dto.CashAction;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Сервис для работы с наличностью.
 */
public interface CashService {
    /**
     * Изменить остаток наличности.
     *
     * @param value сумма изменения
     * @param action действие (снять, положить)
     * @return DTO-представление аккаунта
     */
    Mono<AccountPageDto> editCash(BigDecimal value, CashAction action);
}
