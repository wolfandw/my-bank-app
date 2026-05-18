package io.github.wolfandw.chassis.configuration;

import io.github.wolfandw.chassis.metric.BusinessMetricIncrementor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Авто-конфигурация метрик.
 */
@AutoConfiguration
public class MetricsAutoConfiguration {
    @Bean
    public MeterFilter percentileHistogramFilter() {
        return new MeterFilter() {
            @Override
            public io.micrometer.core.instrument.distribution.DistributionStatisticConfig configure(
                    io.micrometer.core.instrument.Meter.Id id,
                    io.micrometer.core.instrument.distribution.DistributionStatisticConfig config) {

                if (id.getName().endsWith("server.requests")) {
                    return io.micrometer.core.instrument.distribution.DistributionStatisticConfig.builder()
                            .percentilesHistogram(true)
                            .build()
                            .merge(config);
                }
                return config;
            }
        };
    }

    @Bean
    public BusinessMetricIncrementor businessMetricIncrementor(MeterRegistry meterRegistry) {
        return new BusinessMetricIncrementor(meterRegistry);
    }
}
