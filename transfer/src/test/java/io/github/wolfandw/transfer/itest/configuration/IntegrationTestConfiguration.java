package io.github.wolfandw.transfer.itest.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.ReactiveTransactionManager;
import reactor.test.StepVerifier;

@TestConfiguration
public class IntegrationTestConfiguration {
    /**
     * Создает транзакционный враппер для {@link StepVerifier}.
     *
     * @param reactiveTransactionManager менеджер транзакций
     * @return транзакционный враппер для {@link StepVerifier}
     */
    @Bean
    public TrxStepVerifier trxStepVerifier(ReactiveTransactionManager reactiveTransactionManager) {
        return new TrxStepVerifier(reactiveTransactionManager);
    }
}
