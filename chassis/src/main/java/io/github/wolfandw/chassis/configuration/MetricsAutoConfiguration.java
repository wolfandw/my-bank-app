package io.github.wolfandw.chassis.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Авто-конфигурация метрик.
 */
@AutoConfiguration
public class MetricsAutoConfiguration {
    @Bean
    public Counter changeCashGetSuccessCounter(MeterRegistry meterRegistry) {
        return Counter.builder("change_cash_get_total")
                .description("Успешная попытка снятия денег")
                .tag("status", "success")
                .register(meterRegistry);
    }

    @Bean
    public Counter changeCashGetFailureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("change_cash_get_total")
                .description("Неуспешная попытка снятия денег")
                .tag("status", "failure")
                .register(meterRegistry);
    }

    @Bean
    public Counter transferCashSuccessCounter(MeterRegistry meterRegistry) {
        return Counter.builder("transfer_cash_total")
                .description("Успешная попытка перевода денег")
                .tag("status", "success")
                .register(meterRegistry);
    }

    @Bean
    public Counter transferCashFailureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("transfer_cash_total")
                .description("Неуспешная попытка перевода денег")
                .tag("status", "failure")
                .register(meterRegistry);
    }

    @Bean
    public Counter sendUnsentOutboxSuccessCounter(MeterRegistry meterRegistry) {
        return Counter.builder("send_unsent_outbox_total")
                .description("Успешная попытка отправки сообщения в сервис нотификаций")
                .tag("status", "success")
                .register(meterRegistry);
    }

    @Bean
    public Counter sendUnsentOutboxFailureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("send_unsent_outbox_total")
                .description("Неуспешная попытка отправки сообщения в сервис нотификаций")
                .tag("status", "failure")
                .register(meterRegistry);
    }

    @Bean
    public Counter sendUnsentNotificationSuccessCounter(MeterRegistry meterRegistry) {
        return Counter.builder("send_unsent_notification_total")
                .description("Успешная попытка отправки нотификации получателю")
                .tag("status", "success")
                .register(meterRegistry);
    }

    @Bean
    public Counter sendUnsentNotificationFailureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("send_unsent_notification_total")
                .description("Неуспешная попытка отправки нотификации получателю")
                .tag("status", "failure")
                .register(meterRegistry);
    }
}
