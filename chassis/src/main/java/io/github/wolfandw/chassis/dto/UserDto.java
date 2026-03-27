package io.github.wolfandw.chassis.dto;

import java.util.UUID;

/**
 * DTO-модель пользователя.
 *
 * @param id идентификатор
 * @param login логин
 * @param name имя
 * @param birthdate дата рождения
 */
public record UserDto(UUID id, String login, String name, String birthdate) {
}
