package io.github.wolfandw.chassis.dto;

/**
 * DTO-представление операции с аккаунтом.
 *
 * @param accepted признак успешно выполненной операции
 * @param error ошибка, если есть, иначе {@code null}
 * @param info информация, если есть, иначе {@code null}
 */
public record OperationResultDto(boolean accepted, String error, String info) {
}
