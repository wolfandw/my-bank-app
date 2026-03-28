package io.github.wolfandw.chassis.dto;

import java.math.BigDecimal;

/**
 * DTO-представление данных для запроса обновления наличности.
 */
public class ChangeCashRequestDto {
    private String login;
    private BigDecimal value;
    private CashAction action;

    /**
     * Создает DTO-представление данных для запроса обновления наличности.
     *
     * @param login логин
     * @param value сумма
     * @param action действие
     */
    public ChangeCashRequestDto(String login, BigDecimal value, CashAction action) {
        this.login = login;
        this.value = value;
        this.action = action;
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
     * Возвращает действие.
     *
     * @return действие
     */
    public CashAction getAction() {
        return action;
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
     * Устанавливает логин.
     *
     * @param login логин
     */
    public void setLogin(String login) {
        this.login = login;
    }
}
