package io.github.wolfandw.frontui.service;

import io.github.wolfandw.frontui.dto.AccountPageDto;
import io.github.wolfandw.frontui.dto.CashAction;
import reactor.core.publisher.Mono;

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
    Mono<AccountPageDto> editCash(int value, CashAction action);
}
