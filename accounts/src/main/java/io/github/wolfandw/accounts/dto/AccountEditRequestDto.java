package io.github.wolfandw.accounts.dto;

import java.time.LocalDate;

/**
 * DTO-представление данных для запроса обновления аккаунта.
 */
public class AccountEditRequestDto {
    private String name;
    private LocalDate birthDate;

    /**
     * Создает DTO-представление данных для запроса обновления аккаунта.
     *
     * @param name имя
     * @param birthDate дата рождения
     */
    public AccountEditRequestDto(String name, LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
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
    public LocalDate getBirthDate() {
        return birthDate;
    }
}
