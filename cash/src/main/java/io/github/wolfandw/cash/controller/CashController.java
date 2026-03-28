package io.github.wolfandw.cash.controller;

import io.github.wolfandw.chassis.configuration.Constants;
import io.github.wolfandw.chassis.dto.ChangeCashRequestDto;
import io.github.wolfandw.cash.service.CashService;
import io.github.wolfandw.chassis.dto.OperationResultDto;
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
     * @return DTO-модель результата операции
     */
    @PostMapping("/api/cash")
    public Mono<OperationResultDto> editCash(@ModelAttribute ChangeCashRequestDto request) {
        LOG.info("Gateway -> Cash. Получен запрос на изменение наличных");
        return cashService.changeCash(Constants.JWT_USER_STUB, request.getValue(), request.getAction());
    }
}
