package io.github.wolfandw.frontui.controller;

import io.github.wolfandw.frontui.dto.AccountPageDto;
import io.github.wolfandw.frontui.dto.TransfetEditRequestDto;
import io.github.wolfandw.frontui.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
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
    public TransferController( TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Осуществляет перевод получателю.
     *
     * @param request сумма списания и логин пользователя получателя
     * @return шаблон и модель аккаунта текущего пользователя
     */
    @PostMapping("/transfer")
    public Mono<String> transfer(@ModelAttribute TransfetEditRequestDto request) {
        LOG.debug("Пользователь -> Front UI. Получен запрос на перевод наличных");
        Mono<AccountPageDto> accountPageDtoMono = transferService.transfer(request.getValue(), request.getLogin());
        return accountPageDtoMono.map(apd -> "redirect:/account").switchIfEmpty(Mono.just("redirect:/account"));
    }
}
