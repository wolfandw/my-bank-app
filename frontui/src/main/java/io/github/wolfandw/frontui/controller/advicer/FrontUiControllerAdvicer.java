package io.github.wolfandw.frontui.controller.advicer;

import io.github.wolfandw.frontui.exception.FrontUiException;
import io.github.wolfandw.frontui.exception.FrontUiRedirectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

/**
 * Обработчик исключений контроллеров.
 */
@ControllerAdvice
public class FrontUiControllerAdvicer {
    private static final Logger LOG = LoggerFactory.getLogger(FrontUiControllerAdvicer.class);

    private static final String TEMPLATE_ERROR = "error";
    private static final String ATTRIBUTE_ERROR = "error";
    private static final String ATTRIBUTE_STATUS = "status";

    /**
     * Обрабатывает исключение FrontUiRedirectException.
     * Не является критичным для отображается страницы приложения.
     * Отображается на странице приложения.
     *
     * @param e исключение типа FrontUiRedirectException
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(FrontUiRedirectException.class)
    public Mono<String> handleFrontUiRedirectException(FrontUiRedirectException e) {
        LOG.warn(e.getMessage(), e);
        return Mono.just("redirect:/account?" + ATTRIBUTE_ERROR + "=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
    }

    /**
     * Обрабатывает исключение FrontUiException.
     * Является критичным для отображения страницы приложения.
     * Отображается на отдельной странице ошибок.
     *
     * @param e исключение типа FrontUiException
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(FrontUiException.class)
    public Mono<Rendering> handleFrontUiException(FrontUiException e) {
        LOG.error(e.getMessage(), e);
        return Mono.just(
                Rendering.view(TEMPLATE_ERROR)
                        .modelAttribute(ATTRIBUTE_ERROR, e.getMessage())
                        .modelAttribute(ATTRIBUTE_STATUS, HttpStatus.INTERNAL_SERVER_ERROR)
                        .build()
        );
    }

    /**
     * Обрабатывает исключение IllegalArgumentException.
     *
     * @param e исключение типа IllegalArgumentException
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<Rendering> handleIllegalArgumentException(IllegalArgumentException e) {
        return Mono.just(
                Rendering.view(TEMPLATE_ERROR)
                        .modelAttribute(ATTRIBUTE_ERROR, e.getMessage())
                        .modelAttribute(ATTRIBUTE_STATUS, HttpStatus.BAD_REQUEST)
                        .build()
        );
    }

    /**
     * Обрабатывает исключение WebExchangeBindException.
     *
     * @param e исключение типа WebExchangeBindException
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<Rendering> handleWebExchangeBindException(WebExchangeBindException e) {
        return Mono.just(
                Rendering.view(TEMPLATE_ERROR)
                        .modelAttribute(ATTRIBUTE_ERROR, e.getMessage())
                        .modelAttribute(ATTRIBUTE_STATUS, HttpStatus.BAD_REQUEST)
                        .build()
        );
    }

    /**
     * Обрабатывает исключение AccessDeniedException.
     *
     * @param e исключение типа AccessDeniedException
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Mono<Rendering> handleAccessDeniedException(AccessDeniedException e) {
        return Mono.just(
                Rendering.view(TEMPLATE_ERROR)
                        .modelAttribute(ATTRIBUTE_ERROR, e.getMessage())
                        .modelAttribute(ATTRIBUTE_STATUS, HttpStatus.FORBIDDEN)
                        .build()
        );
    }

    /**
     * Обрабатывает исключение NoSuchElementException.
     *
     * @param e исключение типа NoSuchElementException
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(NoSuchElementException.class)
    public Mono<Rendering> handleNoSuchElementException(NoSuchElementException e) {
        return Mono.just(
                Rendering.view(TEMPLATE_ERROR)
                        .modelAttribute(ATTRIBUTE_ERROR, e.getMessage())
                        .modelAttribute(ATTRIBUTE_STATUS, HttpStatus.NOT_FOUND)
                        .build()
        );
    }

    /**
     * Обрабатывает исключение AuthorizationDeniedException.
     *
     * @param e исключение типа AuthorizationDeniedException
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public Mono<Rendering> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return Mono.just(Rendering.redirectTo("/login").build());
    }

    /**
     * Обрабатывает непредвиденные исключения.
     *
     * @param e исключение типа Exception
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(RuntimeException.class)
    public Mono<Rendering> handleGenericException(RuntimeException e) {
        return Mono.just(
                Rendering.view(TEMPLATE_ERROR)
                        .modelAttribute(ATTRIBUTE_ERROR, e.getMessage())
                        .modelAttribute(ATTRIBUTE_STATUS, HttpStatus.INTERNAL_SERVER_ERROR)
                        .build()
        );
    }

    /**
     * Обрабатывает непредвиденные исключения.
     *
     * @param e исключение типа Exception
     * @return имя шаблона ошибки
     */
    @ExceptionHandler(Exception.class)
    public Mono<Rendering> handleGenericException(Exception e) {
        return Mono.just(
                Rendering.view(TEMPLATE_ERROR)
                        .modelAttribute(ATTRIBUTE_ERROR, e.getMessage())
                        .modelAttribute(ATTRIBUTE_STATUS, HttpStatus.INTERNAL_SERVER_ERROR)
                        .build()
        );
    }
}