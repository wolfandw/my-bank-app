package io.github.wolfandw.cash.dto;

import java.util.List;

/**
 * Модель страницы аккаунта.
 */
public class AccountPageDto {
    private String name;
    private String birthdate;
    private int sum;
    private List<AccountDto> accounts;
    private List<String> errors;
    private String info;

    /**
     * Устанавливает имя.
     *
     * @param name имя
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Устанавливает дату рождения.
     *
     * @param birthdate дата рождения
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * Установить сумму.
     *
     * @param sum сумма
     */
    public void setSum(int sum) {
        this.sum = sum;
    }

    /**
     * Установить список пользователей.
     *
     * @param accounts список пользователей
     */
    public void setAccounts(List<AccountDto> accounts) {
        this.accounts = accounts;
    }

    /**
     * Установить список ошибок.
     *
     * @param errors список ошибок
     */
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    /**
     * Установить информационную строку.
     *
     * @param info информационная строка
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * Получить имя.
     *
     * @return имя
     */
    public String getName() {
        return name;
    }

    /**
     * Получить дату рождения.
     *
     * @return дата рождения
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * Получить сумму.
     *
     * @return сумма
     */
    public int getSum() {
        return sum;
    }

    /**
     * получить список пользователей.
     *
     * @return список пользователей
     */
    public List<AccountDto> getAccounts() {
        return accounts;
    }

    /**
     * Получить список ошибок.
     *
     * @return список ошибок
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Получить информационную строку.
     *
     * @return информационная строка
     */
    public String getInfo() {
        return info;
    }
}
