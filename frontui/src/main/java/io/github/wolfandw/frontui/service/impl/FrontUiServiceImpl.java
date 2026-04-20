package io.github.wolfandw.frontui.service.impl;

import io.github.wolfandw.chassis.dto.AccountDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.frontui.service.FrontUiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Function;

/**
 * Реализация {@link FrontUiService}
 */
@Service
public class FrontUiServiceImpl implements FrontUiService {
    private static final Logger LOG = LoggerFactory.getLogger(FrontUiServiceImpl.class);

    private static final String SCHEME = "http";

    private static final String ACCOUNT_PATH = "/api/account";
    private static final String CASH_PATH = "/api/cash";
    private static final String TRANSFER_PATH = "/api/transfer";

    private static final String NAME_PARAMETER = "name";
    private static final String BIRTHDATE_PARAMETER = "birthdate";
    private static final String VALUE_PARAMETER = "value";
    private static final String ACTION_PARAMETER = "action";
    private static final String RECIPIENT_PARAMETER = "recipient";

    private static final String ACCOUNTS_API_UNAVAILABLE = "Сервис счетов недоступен: %s";

    private final WebClient webClient;

    @Value("${gateway.host}")
    private String gatewayHost;

    @Value("${gateway.port}")
    private String gatewayPort;

    /**
     * Создает сервис.
     *
     * @param webClient веб-клиент
     */
    public FrontUiServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Mono<AccountDto> getAccount() {
        LOG.debug("Пользователь -> Front UI. Отправка запроса на получение данных аккаунта");
        return webClient.get()
                .uri(uriBuilder -> getUriBuilder(uriBuilder, ACCOUNT_PATH).build())
                .retrieve()
                .bodyToMono(AccountDto.class)
                .onErrorResume(e -> {
                    LOG.error(ACCOUNTS_API_UNAVAILABLE.formatted(e.getMessage()), e);
                    return Mono.empty();
                });
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Mono<OperationResultDto> changeUserData(String name, LocalDate birthdate) {
        LOG.debug("Front UI -> Gateway. Отправка запроса на изменение данных пользователя");
        return webClient.post()
                .uri(uriBuilder -> builChangeDatadUri(getUriBuilder(uriBuilder, ACCOUNT_PATH), name, birthdate))
                .retrieve()
                .bodyToMono(OperationResultDto.class)
                .onErrorResume(onApiError(ACCOUNTS_API_UNAVAILABLE));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Mono<OperationResultDto> changeCash(BigDecimal value, CashAction action) {
        LOG.debug("Front UI -> Gateway. Отправка запроса на изменение наличных");
        return webClient.post()
                .uri(uriBuilder -> buildChangeCashUri(getUriBuilder(uriBuilder, CASH_PATH), value, action))
                .retrieve()
                .bodyToMono(OperationResultDto.class)
                .onErrorResume(onApiError("Сервис наличных недоступен: %s"));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Mono<OperationResultDto> transferCash(BigDecimal value, String recipient) {
        LOG.debug("Front UI -> Gateway. Отправка запроса на перевод наличных");
        return webClient.post()
                .uri(uriBuilder -> buildTransferCashUri(getUriBuilder(uriBuilder, TRANSFER_PATH), value, recipient))
                .retrieve()
                .bodyToMono(OperationResultDto.class)
                .onErrorResume(onApiError("Сервис переводов недоступен: %s"));
    }

    private UriBuilder getUriBuilder(UriBuilder uriBuilder, String path) {
        return uriBuilder
                .scheme(SCHEME)
                .host(gatewayHost)
                .port(gatewayPort)
                .path(path);
    }

    private URI buildTransferCashUri(UriBuilder uriBuilder, BigDecimal value, String recipient) {
        return uriBuilder
                .queryParam(VALUE_PARAMETER, value)
                .queryParam(RECIPIENT_PARAMETER, recipient)
                .build();
    }

    private URI buildChangeCashUri(UriBuilder uriBuilder, BigDecimal value, CashAction action) {
        return uriBuilder
                .queryParam(VALUE_PARAMETER, value)
                .queryParam(ACTION_PARAMETER, action)
                .build();
    }

    private URI builChangeDatadUri(UriBuilder uriBuilder, String name, LocalDate birthdate) {
        return uriBuilder
                .queryParam(NAME_PARAMETER, name)
                .queryParam(BIRTHDATE_PARAMETER, birthdate)
                .build();
    }

    private Function<Throwable, Mono<? extends OperationResultDto>> onApiError(String service) {
        return e -> {
            String errorMessage = service.formatted(e.getMessage());
            LOG.error(errorMessage, e);
            return Mono.just(new OperationResultDto(new UUID(0, 0), null, false, errorMessage));
        };
    }
}
