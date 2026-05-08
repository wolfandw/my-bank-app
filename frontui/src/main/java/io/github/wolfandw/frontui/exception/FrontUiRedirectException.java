package io.github.wolfandw.frontui.exception;

/**
 * Не критичное исключение для работы приложения в целом.
 * Критичное для отдельных операций.
 */
public class FrontUiRedirectException extends FrontUiException {
    /**
     * Создает новое исключение.
     *
     * @param message входящее сообщение
     * @param cause первопричина исключения
     */
    public FrontUiRedirectException(String message, Throwable cause) {
        super(message,  cause);
    }
}
