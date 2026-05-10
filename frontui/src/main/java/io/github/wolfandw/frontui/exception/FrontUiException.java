package io.github.wolfandw.frontui.exception;

/**
 * Критичное исключение для работы приложения.
 */
public class FrontUiException extends RuntimeException {
    /**
     * Создает новое исключение.
     *
     * @param message входящее сообщение
     * @param cause первопричина исключения
     */
    public FrontUiException(String message, Throwable cause) {
        super(message.formatted(cause.getMessage()),  cause);
    }
}
