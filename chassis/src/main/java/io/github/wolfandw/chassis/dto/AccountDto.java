package io.github.wolfandw.chassis.dto;

/**
 * DTO-представление пользователя.
 *
 * @param login логин
 * @param name имя
 */
public record AccountDto(String login, String name) {
}
