package io.github.wolfandw.accounts.controller;

import io.github.wolfandw.transfer.dto.AccountPageDto;
import io.github.wolfandw.transfer.dto.TransfetEditRequestDto;
import io.github.wolfandw.transfer.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

/**
 * Контроллер для работы с переводами.
 */
@Controller
public class TransferController {
    private static final Logger LOG = LoggerFactory.getLogger(TransferController.class);

    private final TransferService transferService;

    /**
     * Создает контроллер для работы с переводами.
     *
     * @param transferService сервис переводов
     */
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Осуществляет перевод получателю.
     *
     * @param request сумма списания и логин пользователя получателя
     * @return DTO-модель аккаунта текущего пользователя
     */
    @PostMapping("/transfer")
    public Mono<AccountPageDto> transfer(@ModelAttribute TransfetEditRequestDto request) {
        LOG.debug("Gateway -> Transfer. Получен запрос на перевод наличных");
        return transferService.transfer(request.getValue(), request.getLogin());
    }
}
