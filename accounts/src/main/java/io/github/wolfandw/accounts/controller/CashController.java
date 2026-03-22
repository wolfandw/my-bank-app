package io.github.wolfandw.accounts.controller;

import io.github.wolfandw.accounts.dto.AccountPageDto;
import io.github.wolfandw.accounts.dto.CashEditRequestDto;
import io.github.wolfandw.accounts.service.CashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Контроллер для работы с наличностью.
 */
@RestController
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
     * @return DTO-модель аккаунта текущего пользователя
     */
    @PostMapping("/api/cash")
    public Mono<AccountPageDto> editCash(@ModelAttribute CashEditRequestDto request) {
        LOG.error("Получение запроса на изменение наличных в Аккаунт-сервис");
        return cashService.editCash(request.getValue(), request.getAction());
    }
}
