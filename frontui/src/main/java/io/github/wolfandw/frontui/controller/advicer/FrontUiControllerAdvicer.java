package io.github.wolfandw.frontui.controller.advicer;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

/**
 * Обработчик исключений контроллеров.
 */
@ControllerAdvice
public class FrontUiControllerAdvicer {
    private static final String TEMPLATE_ERROR = "error";
    private static final String ATTRIBUTE_ERROR = "error";
    private static final String ATTRIBUTE_STATUS = "status";

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
                        .modelAttribute(ATTRIBUTE_STATUS, HttpServletResponse.SC_BAD_REQUEST)
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
                        .modelAttribute(ATTRIBUTE_STATUS, HttpServletResponse.SC_BAD_REQUEST)
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
                        .modelAttribute(ATTRIBUTE_STATUS, HttpServletResponse.SC_FORBIDDEN)
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
                        .modelAttribute(ATTRIBUTE_STATUS, HttpServletResponse.SC_NOT_FOUND)
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
                        .modelAttribute(ATTRIBUTE_STATUS, HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
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
                        .modelAttribute(ATTRIBUTE_STATUS, HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                        .build()
        );
    }
}