package io.github.wolfandw.accounts.dto;

/**
 * DTO-представление пользователя.
 *
 * @param login логин
 * @param name имя
 */
public record AccountDto(String login, String name) {
}
