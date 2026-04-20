package io.github.wolfandw.transfer.service;

import io.github.wolfandw.chassis.dto.OperationResultDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Сервис переводов.
 */
public interface TransferService {
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
