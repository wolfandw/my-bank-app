package io.github.wolfandw.frontui.dto;

import java.math.BigDecimal;

/**
 * DTO-представление данных для запроса перевода наличности.
 */
public class TransfetEditRequestDto {
    private BigDecimal value;
    private String login;

    /**
     * Создает DTO-представление данных для запроса обновления наличности.
     *
     * @param value сумма
     * @param login логин
     */
    public TransfetEditRequestDto(BigDecimal value, String login) {
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
