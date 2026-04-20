package io.github.wolfandw.notifications;

import io.github.wolfandw.chassis.configuration.OutboxProcessorAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Проложение Notifications.
 */
@SpringBootApplication(exclude = {
        OutboxProcessorAutoConfiguration.class
})
@EnableDiscoveryClient
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
