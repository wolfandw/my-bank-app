package io.github.wolfandw.chassis.dto;

import java.math.BigDecimal;

/**
 * DTO-представление данных для запроса перевода наличности.
 */
public class TransferEditRequestDto {
    private BigDecimal value;
    private String login;

    /**
     * Создает DTO-представление данных для запроса обновления наличности.
     *
     * @param value сумма
     * @param login логин
     */
    public TransferEditRequestDto(BigDecimal value, String login) {
        this.value = value;
        this.login = login;
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
}
