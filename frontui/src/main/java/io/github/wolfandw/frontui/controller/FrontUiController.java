package io.github.wolfandw.frontui.controller;

import io.github.wolfandw.chassis.dto.*;
import io.github.wolfandw.frontui.service.FrontUiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Контроллер аккаунтов.
 */
@Controller
public class FrontUiController {
    private static final Logger LOG = LoggerFactory.getLogger(FrontUiController.class);
    private static final String TEMPLATE_MAIN = "main";
    private static final String ATTRIBUTE_ACCOUNT = "account";
    private static final String ATTRIBUTE_ERRORS = "errors";
    private static final String ATTRIBUTE_INFO = "info";

    private final FrontUiService accountsService;

    /**
     * Создает контроллер для работы с аккаунтами.
     *
     * @param accountsService сервис аккаунтов
     */
    public FrontUiController(FrontUiService accountsService) {
        this.accountsService = accountsService;
    }

    /**
     * Перенаправляет запрос на страницу аккаунта.
     */
    @GetMapping("/")
    public Mono<String> redirectToAccount() {
        return Mono.just("redirect:/account");
    }

    /**
     * Возвращает аккаунт текущего пользователя.
     *
     * @return шаблон и модель аккаунта текущего пользователя
     */
    @GetMapping("/account")
    public Mono<Rendering> getAccount(@RequestParam(required = false) String error,
                                      @RequestParam(required = false) String info) {
        LOG.info("Пользователь -> Front UI. Получен запрос на получение данных аккаунта");
        Mono<AccountDto> accountDto = accountsService.getAccount();
        return accountDto.map(accountDtoMono -> Rendering.view(TEMPLATE_MAIN)
                .modelAttribute(ATTRIBUTE_ACCOUNT, accountDtoMono)
                .modelAttribute(ATTRIBUTE_ERRORS, error == null ? null : List.of(error))
                .modelAttribute(ATTRIBUTE_INFO, info)
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
    public Mono<String> changeUserData(@ModelAttribute ChangeUserDataRequestDto request) {
        LOG.info("Пользователь -> Front UI. Получен запрос на изменение персональных данных");
        Mono<OperationResultDto> accountPageDtoMono = accountsService.changeUserData(request.getName(), request.getBirthdate());
        return accountPageDtoMono.map(this::getRedirect);
    }

    /**
     * Изменяет состояние наличности.
     *
     * @param request сумма списания (пополнения) и действие с наличностью (GET - снять, PUT - пополнить)
     * @return шаблон аккаунта текущего пользователя
     */
    @PostMapping("/cash")
    public Mono<String> changeCash(@ModelAttribute ChangeCashRequestDto request) {
        LOG.info("Пользователь -> Front UI. Получен запрос на изменение наличных");
        Mono<OperationResultDto> accountPageDtoMono = accountsService.changeCash(request.getValue(), request.getAction());
        return accountPageDtoMono.map(this::getRedirect);
    }

    /**
     * Осуществляет перевод получателю.
     *
     * @param request сумма списания и логин пользователя получателя
     * @return шаблон и модель аккаунта текущего пользователя
     */
    @PostMapping("/transfer")
    public Mono<String> transferCash(@ModelAttribute TransferCashRequestDto request) {
        LOG.info("Пользователь -> Front UI. Получен запрос на перевод наличных");
        Mono<OperationResultDto> accountPageDtoMono = accountsService.transferCash(request.getValue(), request.getLogin());
        return accountPageDtoMono.map(this::getRedirect);
    }

    private String getRedirect(OperationResultDto result) {
        if (result.accepted()) {
            return "redirect:/account?info=" + URLEncoder.encode(result.info(), StandardCharsets.UTF_8);
        } else {
            return "redirect:/account?error=" + URLEncoder.encode(result.error(), StandardCharsets.UTF_8);
        }
    }
}
