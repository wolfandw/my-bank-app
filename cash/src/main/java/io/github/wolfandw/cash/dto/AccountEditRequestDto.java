package io.github.wolfandw.cash.dto;

import java.time.LocalDate;

/**
 * DTO-представление данных для запроса обновления аккаунта.
 */
public class AccountEditRequestDto {
    private String name;
    private LocalDate birthdate;

    /**
     * Создает DTO-представление данных для запроса обновления аккаунта.
     *
     * @param name имя
     * @param birthdate дата рождения
     */
    public AccountEditRequestDto(String name, LocalDate birthdate) {
        this.name = name;
        this.birthdate = birthdate;
    }

    /**
     * Возвращает имя.
     *
     * @return имя
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает дату рождения.
     *
     * @return дата рождения
     */
    public LocalDate getBirthdate() {
        return birthdate;
    }
}
