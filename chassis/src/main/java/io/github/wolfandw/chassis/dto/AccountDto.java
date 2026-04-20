package io.github.wolfandw.chassis.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO-модель аккаунта.
 */
public record AccountDto(UUID id, UserDto user, BigDecimal balance,  List<UserDto> users) {
}
