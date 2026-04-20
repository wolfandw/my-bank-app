package io.github.wolfandw.chassis.dto;

import java.time.LocalDate;

/**
 * DTO-представление данных для запроса обновления аккаунта.
 */
public class ChangeUserDataRequestDto {
    private String login;
    private String name;
    private LocalDate birthdate;

    /**
     * Создает DTO-представление данных для запроса обновления аккаунта.
     *
     * @param login логин
     * @param name имя
     * @param birthdate дата рождения
     */
    public ChangeUserDataRequestDto(String login, String name, LocalDate birthdate) {
        this.login = login;
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
