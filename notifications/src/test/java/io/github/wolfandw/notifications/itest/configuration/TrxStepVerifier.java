package io.github.wolfandw.notifications.itest.configuration;

import org.reactivestreams.Publisher;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.test.StepVerifier;

/**
 * Транзакционный враппер для {@link StepVerifier}.
 */
public class TrxStepVerifier {
    private final ReactiveTransactionManager reactiveTransactionManager;

    /**
     * Создает транзакционный враппер для {@link StepVerifier}.
     *
     * @param transactionManager менеджер транзакций
     */
    public TrxStepVerifier(ReactiveTransactionManager transactionManager) {
        this.reactiveTransactionManager = transactionManager;
    }

    /**
     * Подготавливает транзакционный враппер.
     *
     * @param publisher паблишер для подписки и проверки
     * @return билдер для проверки
     */
    public <T> StepVerifier.FirstStep<T> create(Publisher<? extends T> publisher) {
        return StepVerifier.create(
                TransactionalOperator.create(reactiveTransactionManager)
                        .execute(trx -> {
                            trx.setRollbackOnly();
                            return publisher;
                        })
        );
    }
}
