package io.github.wolfandw.transfer.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Kонфигурация метрик.
 */
@Configuration
public class MetricsConfiguration {
    @Bean
    public Counter sendUnsentOutboxSuccessCounter(MeterRegistry meterRegistry) {
        return Counter.builder("send_unsent_outbox_total")
                .description("Попытка отправки сообщения в сервис нотификаций")
                .tag("status", "success")
                .register(meterRegistry);
    }

    @Bean
    public Counter sendUnsentOutboxFailureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("send_unsent_outbox_total")
                .description("Попытка отправки сообщения в сервис нотификаций")
                .tag("status", "failure")
                .register(meterRegistry);
    }
}
