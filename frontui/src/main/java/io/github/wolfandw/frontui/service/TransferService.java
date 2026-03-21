package io.github.wolfandw.frontui.service;

import io.github.wolfandw.frontui.dto.AccountPageDto;
import reactor.core.publisher.Mono;

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
    Mono<AccountPageDto> transfer(int value, String login);
}
