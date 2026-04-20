package io.github.wolfandw.chassis.dto;

import java.util.List;

/**
 * Модель страницы аккаунта.
 */
public record AccountPageDto(AccountDto account, List<String> errors,  String info) {
}
