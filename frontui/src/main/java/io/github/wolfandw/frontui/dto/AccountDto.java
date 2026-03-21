package io.github.wolfandw.frontui.dto;

/**
 * DTO-представление пользователя.
 *
 * @param login логин
 * @param name имя
 */
public record AccountDto(String login, String name) {
}
