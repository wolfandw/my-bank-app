package io.github.wolfandw.chassis.dto;

import java.math.BigDecimal;

/**
 * DTO-представление данных для запроса обновления наличности.
 */
public class ChangeCashRequestDto {
    private BigDecimal value;
    private CashAction action;

    /**
     * Создает DTO-представление данных для запроса обновления наличности.
     *
     * @param value сумма
     * @param action действие
     */
    public ChangeCashRequestDto(BigDecimal value, CashAction action) {
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
}
