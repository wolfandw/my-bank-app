package io.github.wolfandw.accounts.controller;

import io.github.wolfandw.accounts.service.UserService;
import io.github.wolfandw.chassis.dto.ChangeUserDataRequestDto;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Rest-контроллер пользователей.
 */
@RestController
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * Создает контроллер для работы с пользователями.
     *
     * @param userService сервис пользователей
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Изменяет имя и дату рождения.
     *
     * @param request параметры запроса
     * @param jwt jwt токен
     * @return DTO-модель результата операции
     */
    @PostMapping("/api/account")
    @PreAuthorize("hasRole('USER') and hasRole('ACCOUNTS_WRITE')")
    public Mono<OperationResultDto> changeUserData(@ModelAttribute ChangeUserDataRequestDto request,
                                                   @AuthenticationPrincipal Jwt jwt) {
        LOG.debug("Gateway -> Accounts. Получен запрос на изменение персональных данных");
        return userService.changeUserData(jwt.getClaim("preferred_username"), request.getName(), request.getBirthdate());
    }
}
