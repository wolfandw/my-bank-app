package io.github.wolfandw.transfer.controller;

import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.dto.TransferCashRequestDto;
import io.github.wolfandw.transfer.service.TransferService;
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
 * Контроллер для работы с переводами.
 */
@RestController
public class TransferController {
    private static final Logger LOG = LoggerFactory.getLogger(TransferController.class);

    private final TransferService transferService;

    /**
     * Создает контроллер для работы с переводами.
     *
     * @param transferService сервис переводов
     */
    public TransferController( TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Осуществляет перевод получателю.
     *
     * @param request сумма списания и логин пользователя получателя
     * @return DTO-модель результата операции
     */
    @PostMapping("/api/transfer")
    @PreAuthorize("hasRole('USER') and hasRole('TRANSFER_WRITE')")
    public Mono<OperationResultDto> transfer(@ModelAttribute TransferCashRequestDto request,
                                             @AuthenticationPrincipal Jwt jwt) {
        LOG.debug("Gateway -> Transfer. Получен запрос на перевод наличных");
        return transferService.transferCash(jwt.getClaim("preferred_username"), request.getValue(), request.getRecipient());
    }
}
