package io.github.wolfandw.notifications.configuration;

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
    public Counter sendUnsentNotificationSuccessCounter(MeterRegistry meterRegistry) {
        return Counter.builder("send_unsent_notification_total")
                .description("Попытка отправки нотификации получателю")
                .tag("status", "success")
                .register(meterRegistry);
    }

    @Bean
    public Counter sendUnsentNotificationFailureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("send_unsent_notification_total")
                .description("Попытка отправки нотификации получателю")
                .tag("status", "failure")
                .register(meterRegistry);
    }
}
