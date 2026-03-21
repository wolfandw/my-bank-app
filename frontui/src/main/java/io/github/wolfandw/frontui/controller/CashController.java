package io.github.wolfandw.frontui.controller;

import io.github.wolfandw.frontui.dto.AccountPageDto;
import io.github.wolfandw.frontui.dto.CashAction;
import io.github.wolfandw.frontui.service.CashService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

/**
 * Контроллер для работы с наличностью.
 */
@Controller
public class CashController {
    private static final String TEMPLATE_MAIN = "main";
    private static final String ATTRIBUTE_ACCOUNT_PAGE = "accountPage";

    private final CashService cashService;

    /**
     * Создает контроллер для работы с наличностью.
     *
     * @param cashService сервис наличности
     */
    public CashController(CashService cashService) {
        this.cashService = cashService;
    }

    /**
     * Изменяет состояние наличности.
     *
     * @param value сумма списания (пополнения)
     * @param action действие с наличностью (GET - снять, PUT - пополнить)
     * @return шаблон и модель аккаунта текущего пользователя
     */
    @PostMapping("/cash")
    public Mono<Rendering> editCash(@RequestParam("value") int value, @RequestParam("action") CashAction action) {
        Mono<AccountPageDto> accountPageDtoMono = cashService.editCash(value, action);
        return Mono.just(Rendering.view(TEMPLATE_MAIN)
                .modelAttribute(ATTRIBUTE_ACCOUNT_PAGE, accountPageDtoMono)
                .build()
        );
    }
}
