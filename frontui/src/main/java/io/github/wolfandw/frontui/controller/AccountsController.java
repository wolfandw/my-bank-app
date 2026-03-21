package io.github.wolfandw.frontui.controller;

import io.github.wolfandw.frontui.dto.AccountPageDto;
import io.github.wolfandw.frontui.service.AccountsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Контроллер аккаунтов.
 */
@Controller
public class AccountsController {
    private static final String TEMPLATE_MAIN = "main";
    private static final String ATTRIBUTE_ACCOUNT_PAGE = "accountPage";

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
    public Mono<Rendering> getAccount() {
        Mono<AccountPageDto> accountPageDtoMono = accountsService.getAccount();
        return Mono.just(Rendering.view(TEMPLATE_MAIN)
                        .modelAttribute(ATTRIBUTE_ACCOUNT_PAGE, accountPageDtoMono)
                        .build()
        );
    }

    /**
     * Изменяет имя и дату рождения.
     *
     * @param name      имя
     * @param birthdate дата рождения
     * @return шаблон и модель аккаунта текущего пользователя
     */
    @PostMapping("/account")
    public Mono<Rendering> editAccount(@RequestParam("name") String name, @RequestParam("birthdate") LocalDate birthdate) {
        Mono<AccountPageDto> accountPageDtoMono = accountsService.editAccount(name, birthdate);
        return Mono.just(Rendering.view(TEMPLATE_MAIN)
                        .modelAttribute(ATTRIBUTE_ACCOUNT_PAGE, accountPageDtoMono)
                        .build()
        );
    }
}
