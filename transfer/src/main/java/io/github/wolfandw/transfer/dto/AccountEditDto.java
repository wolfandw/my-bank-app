package io.github.wolfandw.transfer.dto;

import java.time.LocalDate;

/**
 * DTO-представление для обновления данных аккаунта.
 */
public class AccountEditDto {
    private String name;
    private LocalDate birthdate;

    /**
     * Создает DTO-представление для обновления данных аккаунта.
     *
     * @param name имя
     * @param birthdate дата рождения
     */
    public AccountEditDto(String name, LocalDate birthdate) {
        this.name = name;
        this.birthdate = birthdate;
    }
}
