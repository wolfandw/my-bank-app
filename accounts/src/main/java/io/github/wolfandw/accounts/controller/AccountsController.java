package io.github.wolfandw.accounts.controller;

import io.github.wolfandw.accounts.service.AccountsService;
import io.github.wolfandw.chassis.dto.AccountDto;
import io.github.wolfandw.chassis.dto.ChangeCashRequestDto;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.dto.TransferCashRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
     * @param jwt jwt токен
     * @return DTO-модель счета текущего пользователя
     */
    @GetMapping("/api/account")
    @PreAuthorize("hasRole('USER')")
    public Mono<AccountDto> getAccount(@AuthenticationPrincipal Jwt jwt) {
        LOG.debug("Gateway -> Accounts. Получен запрос на получение данных аккаунта");
        return accountsService.getAccount(jwt.getClaimAsString("preferred_username"));
    }

    /**
     * Изменяет состояние наличности.
     *
     * @param request сумма списания (пополнения) и действие с наличностью (GET - снять, PUT - пополнить)
     * @return DTO-модель результата операции
     */
    @PostMapping("/api/cash")
    @PreAuthorize("hasRole('CASH_WRITE') and hasRole('ACCOUNTS_SERVICE_CLIENT')")
    public Mono<OperationResultDto> changeCash(@ModelAttribute ChangeCashRequestDto request) {
        LOG.debug("Cash -> Accounts. Получен запрос на изменение наличных");
        return accountsService.changeCash(request.getLogin(), request.getValue(), request.getAction());
    }

    /**
     * Осуществляет перевод наличных получателю.
     *
     * @param request сумма списания и логин пользователя получателя
     * @return DTO-модель результата операции
     */
    @PostMapping("/api/transfer")
    @PreAuthorize("hasRole('TRANSFER_WRITE') and hasRole('ACCOUNTS_SERVICE_CLIENT')")
    public Mono<OperationResultDto> transferCash(@ModelAttribute TransferCashRequestDto request) {
        LOG.debug("Transfer -> Accounts. Получен запрос на перевод наличных");
        return accountsService.transferCash(request.getLogin(), request.getValue(), request.getRecipient());
    }
}
