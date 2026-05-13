package io.github.wolfandw.chassis.configuration;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Авто-конфигурация логирования.
 */
@AutoConfiguration
public class LoggingAutoConfiguration {
    private static final String TASKS_SCHEDULED_EXECUTION = "tasks.scheduled.execution";

    @Bean
    public ObservationPredicate noScheduledPredicate() {
        return (name, context) -> !TASKS_SCHEDULED_EXECUTION.equals(name);
    }
}
