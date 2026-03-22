package io.github.wolfandw.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Проложение Notifications.
 */
@SpringBootApplication
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
