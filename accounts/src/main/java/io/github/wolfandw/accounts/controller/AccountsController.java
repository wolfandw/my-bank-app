package io.github.wolfandw.accounts.controller;

import io.github.wolfandw.accounts.dto.AccountPageDto;
import io.github.wolfandw.accounts.service.AccountsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Rest-контроллер аккаунтов.
 */
@RestController
public class AccountsController {
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
     * @return шаблон и модель аккаунта текущего пользователя
     */
    @GetMapping("/account")
    public Mono<AccountPageDto> getAccount() {
        return accountsService.getAccount();
    }

    /**
     * Изменяет имя и дату рождения.
     *
     * @param name      имя
     * @param birthdate дата рождения
     * @return шаблон и модель аккаунта текущего пользователя
     */
    @PostMapping("/account")
    public Mono<AccountPageDto> editAccount(@RequestParam("name") String name, @RequestParam("birthdate") LocalDate birthdate) {
       return accountsService.editAccount(name, birthdate);
    }
}
