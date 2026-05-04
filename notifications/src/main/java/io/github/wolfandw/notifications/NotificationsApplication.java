package io.github.wolfandw.notifications;

import io.github.wolfandw.chassis.configuration.OutboxProcessorAutoConfiguration;
import io.github.wolfandw.chassis.configuration.SecurityWebFilterConfiguration;
import io.github.wolfandw.chassis.configuration.WebClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Проложение Notifications.
 */
@SpringBootApplication(exclude = {
        OutboxProcessorAutoConfiguration.class,
        WebClientConfiguration.class
})
@EnableR2dbcRepositories(basePackages = {"io.github.wolfandw.notifications.repository"})
public class NotificationsApplication {
    /**
     * Запускает приложение.
     *
     * @param args аргументы
     */
    public static void main(String[] args) {
        SpringApplication.run(NotificationsApplication.class, args);
    }
}
