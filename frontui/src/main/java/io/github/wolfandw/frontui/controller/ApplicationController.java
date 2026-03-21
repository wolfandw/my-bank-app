package io.github.wolfandw.frontui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

/**
 * Контроллер приложения.
 */
@Controller
public class ApplicationController {
    /**
     * Перенаправляет запрос на страницу аккаунта.
     */
    @GetMapping
    public Mono<String> redirectToAccount() {
        return  Mono.just("redirect:/account");
    }
}
