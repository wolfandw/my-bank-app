package io.github.wolfandw.chassis.configuration;

import io.github.wolfandw.chassis.filter.LoggingWebFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Авто-конфигурация логирования.
 */
@AutoConfiguration
public class LoggingAutoConfiguration {
    @Bean
    public LoggingWebFilter loggingWebFilter() {
        return new LoggingWebFilter();
    }
}
