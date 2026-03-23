package io.github.wolfandw.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Проложение Notifications.
 */
@SpringBootApplication
@EnableDiscoveryClient
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
