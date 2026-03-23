package io.github.wolfandw.accounts.controller;

import io.github.wolfandw.accounts.service.TransferService;
import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.chassis.dto.TransferEditRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Контроллер для работы с переводами.
 */
@RestController
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
    @PostMapping("/api/transfer")
    public Mono<AccountPageDto> transfer(@ModelAttribute TransferEditRequestDto request) {
        LOG.info("Transfer -> Accounts. Получен запрос на перевод наличных");
        return transferService.transfer(request.getValue(), request.getLogin());
    }
}
