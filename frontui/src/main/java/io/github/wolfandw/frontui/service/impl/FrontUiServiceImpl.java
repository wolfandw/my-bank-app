package io.github.wolfandw.frontui.service.impl;

import io.github.wolfandw.chassis.dto.AccountDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.frontui.service.FrontUiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.function.Function;

/**
 * Реализация {@link FrontUiService}
 */
@Service
public class FrontUiServiceImpl implements FrontUiService {
    private static final Logger LOG = LoggerFactory.getLogger(FrontUiServiceImpl.class);

    private static final String SCHEME = "http";

    private static final String ACCOUNT_PATH = "/account";
    private static final String CASH_PATH = "/cash";
    private static final String TRANSFER_PATH = "/transfer";

    private static final String NAME_PARAMETER = "name";
    private static final String BIRTHDATE_PARAMETER = "birthdate";
    private static final String VALUE_PARAMETER = "value";
    private static final String ACTION_PARAMETER = "action";
    private static final String LOGIN_PARAMETER = "login";

    private static final String ACCOUNTS_API_UNAVAILABLE = "Сервис счетов недоступен: {0}";
    private static final String CASH_API_UNAVAILABLE = "Сервис наличных недоступен: {0}";
    private static final String TRANSFER_API_UNAVAILABLE = "Сервис переводов недоступен: {0}";

    private final WebClient loadBalancedWebClient;

    @Value("${gateway.host}")
    private String gatewayHost;

    @Value("${gateway.port}")
    private String gatewayPort;

    /**
     * Создает сервис.
     *
     * @param loadBalancedWebClient веб-клиент
     */
    public FrontUiServiceImpl(WebClient loadBalancedWebClient) {
        this.loadBalancedWebClient = loadBalancedWebClient;
    }

    @Override
    public Mono<AccountDto> getAccount() {
        LOG.info("Пользователь -> Front UI. Отправка запроса на получение данных аккаунта");
        return loadBalancedWebClient.get()
                .uri(uriBuilder -> getUriBuilder(uriBuilder, ACCOUNT_PATH).build())
                .retrieve()
                .bodyToMono(AccountDto.class)
                .onErrorResume(e -> {
                    LOG.error(MessageFormat.format(ACCOUNTS_API_UNAVAILABLE, e.getMessage()), e);
                    return Mono.empty();
                });
    }

    @Override
    public Mono<OperationResultDto> changeUserData(String name, LocalDate birthdate) {
        LOG.info("Front UI -> Gateway. Отправка запроса на изменение данных пользователя");
        return loadBalancedWebClient.post()
                .uri(uriBuilder -> buildUri(getUriBuilder(uriBuilder, ACCOUNT_PATH), name, birthdate))
                .retrieve()
                .bodyToMono(OperationResultDto.class)
                .onErrorResume(onApiError(ACCOUNTS_API_UNAVAILABLE));
    }

    @Override
    public Mono<OperationResultDto> changeCash(BigDecimal value, CashAction action) {
        LOG.info("Front UI -> Gateway. Отправка запроса на изменение наличных");
        return loadBalancedWebClient.post()
                .uri(uriBuilder -> buildUri(getUriBuilder(uriBuilder, CASH_PATH), value, action))
                .retrieve()
                .bodyToMono(OperationResultDto.class)
                .onErrorResume(onApiError(CASH_API_UNAVAILABLE));
    }

    @Override
    public Mono<OperationResultDto> transferCash(BigDecimal value, String login) {
        LOG.info("Front UI -> Gateway. Отправка запроса на перевод наличных");
        return loadBalancedWebClient.post()
                .uri(uriBuilder -> buildUri(getUriBuilder(uriBuilder, TRANSFER_PATH), value, login))
                .retrieve()
                .bodyToMono(OperationResultDto.class)
                .onErrorResume(onApiError(TRANSFER_API_UNAVAILABLE));
    }

    private UriBuilder getUriBuilder(UriBuilder uriBuilder, String path) {
        return uriBuilder
                .scheme(SCHEME)
                .host(gatewayHost)
                .port(gatewayPort)
                .path(path);
    }

    private URI buildUri(UriBuilder uriBuilder, BigDecimal value, String login) {
        return uriBuilder
                .queryParam(VALUE_PARAMETER, value)
                .queryParam(LOGIN_PARAMETER, login)
                .build();
    }

    private URI buildUri(UriBuilder uriBuilder, BigDecimal value, CashAction action) {
        return uriBuilder
                .queryParam(VALUE_PARAMETER, value)
                .queryParam(ACTION_PARAMETER, action)
                .build();
    }

    private URI buildUri(UriBuilder uriBuilder, String name, LocalDate birthdate) {
        return uriBuilder
                .queryParam(NAME_PARAMETER, name)
                .queryParam(BIRTHDATE_PARAMETER, birthdate)
                .build();
    }

    private Function<Throwable, Mono<? extends OperationResultDto>> onApiError(String service) {
        return e -> {
            String errorMessage = MessageFormat.format(service, e.getMessage());
            LOG.error(errorMessage, e);
            return Mono.just(new OperationResultDto(false, errorMessage, null));
        };
    }
}
