package io.github.wolfandw.chassis.dto;

import java.util.UUID;

/**
 * DTO-представление операции с аккаунтом.
 *
 * @param userId идентификатор пользователя
 * @param login логин пользователя
 * @param accepted признак успешно выполненной операции или отказа
 * @param message сообщение операции (ошибка или информация, в зависимости от {@param accepted}
 */
public record OperationResultDto(UUID userId, String login, boolean accepted, String message) {
}
