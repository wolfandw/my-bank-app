package io.github.wolfandw.transfer.dto;

import java.time.LocalDate;

/**
 * DTO-представление для обновления данных аккаунта.
 */
public class AccountEditDto {
    private String name;
    private LocalDate birthDate;

    /**
     * Создает DTO-представление для обновления данных аккаунта.
     *
     * @param name имя
     * @param birthDate дата рождения
     */
    public AccountEditDto(String name, LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }
}
