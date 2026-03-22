package io.github.wolfandw.transfer.service;

import io.github.wolfandw.transfer.dto.AccountPageDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Сервис переводов.
 */
public interface TransferService {
    /**
     * Перевести наличные.
     *
     * @param value сумма перевода
     * @param login получатель перевода
     * @return DTO-представление аккаунта
     */
    Mono<AccountPageDto> transfer(BigDecimal value, String login);
}
