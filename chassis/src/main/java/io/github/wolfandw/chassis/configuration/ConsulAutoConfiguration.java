package io.github.wolfandw.chassis.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Авто-конфигурация для Consul.
 */
@AutoConfiguration
@ConditionalOnProperty(name = "spring.cloud.consul.enabled", matchIfMissing = true)
public class ConsulAutoConfiguration {
}
