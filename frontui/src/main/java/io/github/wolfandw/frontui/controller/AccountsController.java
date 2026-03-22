package io.github.wolfandw.frontui.controller;

import io.github.wolfandw.frontui.dto.AccountEditRequestDto;
import io.github.wolfandw.frontui.dto.AccountPageDto;
import io.github.wolfandw.frontui.service.AccountsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

/**
 * Контроллер аккаунтов.
 */
@Controller
public class AccountsController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountsController.class);
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
     * @param request имя и дата рождения
     * @return шаблон текущего пользователя
     */
    @PostMapping("/account")
    public Mono<String> editAccount(@ModelAttribute AccountEditRequestDto request) {
        LOG.debug("Пользователь -> Front UI. Получен запрос на изменение персональных данных");
        Mono<AccountPageDto> accountPageDtoMono = accountsService.editAccount(request.getName(), request.getBirthdate());
        return accountPageDtoMono.map(apd -> "redirect:/account").switchIfEmpty(Mono.just("redirect:/account"));
    }
}
