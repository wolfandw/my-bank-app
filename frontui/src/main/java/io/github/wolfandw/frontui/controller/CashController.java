package io.github.wolfandw.frontui.controller;

import io.github.wolfandw.frontui.dto.AccountPageDto;
import io.github.wolfandw.frontui.dto.CashEditRequestDto;
import io.github.wolfandw.frontui.service.CashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

/**
 * Контроллер для работы с наличностью.
 */
@Controller
public class CashController {
    private static final Logger LOG = LoggerFactory.getLogger(CashController.class);

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
     * @param request сумма списания (пополнения) и действие с наличностью (GET - снять, PUT - пополнить)
     * @return шаблон аккаунта текущего пользователя
     */
    @PostMapping("/cash")
    public Mono<String> editCash(@ModelAttribute CashEditRequestDto request) {
        LOG.debug("Пользователь -> Front UI. Получен запрос на изменение наличных");
        Mono<AccountPageDto> accountPageDtoMono = cashService.editCash(request.getValue(), request.getAction());
        return accountPageDtoMono.map(apd -> "redirect:/account").switchIfEmpty(Mono.just("redirect:/account"));
    }
}
