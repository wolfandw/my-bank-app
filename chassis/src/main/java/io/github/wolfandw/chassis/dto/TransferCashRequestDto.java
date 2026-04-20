package io.github.wolfandw.chassis.dto;

import java.math.BigDecimal;

/**
 * DTO-представление данных для запроса перевода наличности.
 */
public class TransferCashRequestDto {
    private String login;
    private BigDecimal value;
    private String recipient;

    /**
     * Создает DTO-представление данных для запроса обновления наличности.
     *
     * @param login логин
     * @param value сумма
     * @param recipient логин получателя
     */
    public TransferCashRequestDto(String login, BigDecimal value, String recipient) {
        this.login = login;
        this.value = value;
        this.recipient = recipient;
    }

    /**
     * Возвращает сумму.
     *
     * @return сумма
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Возвращает логин.
     *
     * @return логин
     */
    public String getLogin() {
        return login;
    }

    /**
     * Возвращает логин.
     *
     * @return recipient
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Устанавливает логин.
     *
     * @param recipient логин
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
