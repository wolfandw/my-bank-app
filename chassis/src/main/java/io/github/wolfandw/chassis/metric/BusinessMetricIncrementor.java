package io.github.wolfandw.chassis.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Компонент регистрации метрик.
 */
public class BusinessMetricIncrementor {
    private static final String SUCCESS = "Success";
    private static final String FAILURE = "Failure";

    private final MeterRegistry meterRegistry;

    /**
     * Создает компонент регистрации метрик.
     *
     * @param meterRegistry реестр метрик
     */
    public BusinessMetricIncrementor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Увеличивает счетчик попыток снятия денег Success.
     *
     * @param login логин
     */
    public void incrementChangeCashSuccess(String login) {
        incrementChangeCashFailure(SUCCESS, login);
    }

    /**
     * Увеличивает счетчик попыток снятия денег Failure.
     *
     * @param login логин
     */
    public void incrementChangeCashFailure(String login) {
        incrementChangeCashFailure(FAILURE, login);
    }



    /**
     * Увеличивает счетчик попыток перевода денег Success.
     *
     * @param login логин
     * @param recipient логин получателя
     */
    public void incrementTransferCashSuccess(String login, String recipient) {
        incrementTransferCash(SUCCESS, login, recipient);
    }

    /**
     * Увеличивает счетчик попыток перевода денег Failure.
     *
     * @param login логин
     * @param recipient логин получателя
     */
    public void incrementTransferCashFailure(String login, String recipient) {
        incrementTransferCash(FAILURE, login, recipient);
    }



    /**
     * Увеличивает счетчик попыток отправки сообщений в сервис нотификаций Success.
     *
     * @param login логин
     */
    public void incrementOutboxSuccess(String login) {
        incrementOutbox(SUCCESS, login);
    }

    /**
     * Увеличивает счетчик попыток отправки сообщений в сервис нотификаций Failure.
     *
     * @param login логин
     */
    public void incrementOutboxFailure(String login) {
        incrementOutbox(FAILURE, login);
    }



    /**
     * Увеличивает счетчик попыток отправки нотификаций получателю Success.
     *
     * @param login логин
     */
    public void incrementNotificationSuccess(String login) {
        incrementNotification(SUCCESS, login);
    }

    /**
     * Увеличивает счетчик попыток отправки нотификаций получателю Failure.
     *
     * @param login логин
     */
    public void incrementNotificationFailure(String login) {
        incrementNotification(FAILURE, login);
    }

    private void incrementChangeCashFailure(String status, String login) {
        Counter.builder("change_cash_get_total")
                .description("Попытка снятия денег")
                .tag("status", status)
                .tag("login", login)
                .register(meterRegistry)
                .increment();
    }

    private void incrementTransferCash(String status, String login, String recipient) {
        Counter.builder("transfer_cash_total")
                .description("Попытка перевода денег")
                .tag("status", status)
                .tag("login", login)
                .tag("recipient", recipient)
                .register(meterRegistry)
                .increment();
    }

    private void incrementOutbox(String status, String login) {
        Counter.builder("send_unsent_outbox_total")
                .description("Попытка отправки сообщения в сервис нотификаций")
                .tag("status", status)
                .tag("login", login)
                .register(meterRegistry)
                .increment();
    }

    private void incrementNotification(String status, String login) {
        Counter.builder("send_unsent_notification_total")
                .description("Попытка отправки нотификации получателю")
                .tag("status", status)
                .tag("login", login)
                .register(meterRegistry)
                .increment();
    }
}
