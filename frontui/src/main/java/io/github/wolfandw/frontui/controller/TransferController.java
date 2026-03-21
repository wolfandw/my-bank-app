package io.github.wolfandw.frontui.controller;

import io.github.wolfandw.frontui.dto.AccountPageDto;
import io.github.wolfandw.frontui.service.TransferService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

/**
 * Контроллер для работы с переводами.
 */
@Controller
public class TransferController {
    private static final String TEMPLATE_MAIN = "main";
    private static final String ATTRIBUTE_ACCOUNT_PAGE = "accountPage";

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
     * @param value сумма списания
     * @param login логин пользователя получателя
     * @return шаблон и модель аккаунта текущего пользователя
     */
    @PostMapping("/transfer")
    public Mono<Rendering> transfer(@RequestParam("value") int value, @RequestParam("login") String login) {
        Mono<AccountPageDto> accountPageDtoMono = transferService.transfer(value, login);
        return Mono.just(Rendering.view(TEMPLATE_MAIN)
                .modelAttribute(ATTRIBUTE_ACCOUNT_PAGE, accountPageDtoMono)
                .build()
        );
    }
}
