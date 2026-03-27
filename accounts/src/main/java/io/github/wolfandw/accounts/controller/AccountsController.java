package io.github.wolfandw.accounts.controller;

import io.github.wolfandw.chassis.dto.*;
import io.github.wolfandw.accounts.service.AccountsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Rest-контроллер аккаунтов.
 */
@RestController
public class AccountsController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountsController.class);

    private final AccountsService accountsService;

    /**
     * Создает контроллер для работы с аккаунтами.
     *
     * @param accountsService сервис аккаунтов
     */
    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    /**
     * Возвращает счет текущего пользователя.
     *
     * @return DTO-модель счета текущего пользователя
     */
    @GetMapping("/api/account")
    public Mono<AccountDto> getAccount() {
        LOG.info("Gateway -> Accounts. Получен запрос на получение данных аккаунта");
        return accountsService.getAccount("user");
    }

    /**
     * Изменяет состояние наличности.
     *
     * @param request сумма списания (пополнения) и действие с наличностью (GET - снять, PUT - пополнить)
     * @return DTO-модель результата операции
     */
    @PostMapping("/api/cash")
    public Mono<OperationResultDto> changeCash(@ModelAttribute ChangeCashRequestDto request) {
        LOG.info("Cash -> Accounts. Получен запрос на изменение наличных");
        return accountsService.changeCash("user", request.getValue(), request.getAction());
    }

    /**
     * Осуществляет перевод наличных получателю.
     *
     * @param request сумма списания и логин пользователя получателя
     * @return DTO-модель результата операции
     */
    @PostMapping("/api/transfer")
    public Mono<OperationResultDto> transferCash(@ModelAttribute TransferCashRequestDto request) {
        LOG.info("Transfer -> Accounts. Получен запрос на перевод наличных");
        return accountsService.transferCash("user", request.getValue(), request.getLogin());
    }
}
