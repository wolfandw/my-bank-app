package io.github.wolfandw.gateway;

import io.github.wolfandw.chassis.configuration.KafkaProducerAutoConfiguration;
import io.github.wolfandw.chassis.configuration.OutboxProcessorAutoConfiguration;
import io.github.wolfandw.chassis.configuration.SecurityWebFilterConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.r2dbc.autoconfigure.DataR2dbcRepositoriesAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration;
import org.springframework.boot.r2dbc.autoconfigure.R2dbcAutoConfiguration;
import reactor.core.publisher.Hooks;

/**
 * Приложение Gateway.
 */
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		R2dbcAutoConfiguration.class,
		DataR2dbcRepositoriesAutoConfiguration.class,
		LiquibaseAutoConfiguration.class,
		OutboxProcessorAutoConfiguration.class,
		SecurityWebFilterConfiguration.class,
		KafkaProducerAutoConfiguration.class
})
public class GatewayApplication {
    /**
	 * Запускает приложение.
	 *
     * @param args аргументы
     */
	public static void main(String[] args) {
		Hooks.enableAutomaticContextPropagation();
		SpringApplication.run(GatewayApplication.class, args);
	}
}
