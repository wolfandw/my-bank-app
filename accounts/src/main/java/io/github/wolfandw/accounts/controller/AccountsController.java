package io.github.wolfandw.accounts.controller;

import io.github.wolfandw.chassis.dto.AccountEditRequestDto;
import io.github.wolfandw.chassis.dto.AccountPageDto;
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
     * Возвращает аккаунт текущего пользователя.
     *
     * @return DTO-модель аккаунта текущего пользователя
     */
    @GetMapping("/api/account")
    public Mono<AccountPageDto> getAccount() {
        LOG.info("Gateway -> Accounts. Получен запрос на получение данных аккаунта");
        return accountsService.getAccount();
    }

    /**
     * Изменяет имя и дату рождения.
     *
     * @param request параметры запроса
     * @return DTO-модель аккаунта текущего пользователя
     */
    @PostMapping("/api/account")
    public Mono<AccountPageDto> editAccount(@ModelAttribute AccountEditRequestDto request) {
        LOG.info("Gateway -> Accounts. Получен запрос на изменение персональных данных");
       return accountsService.editAccount(request.getName(), request.getBirthdate());
    }
}
