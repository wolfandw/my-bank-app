package io.github.wolfandw.frontui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Проложение Front UI.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class FrontUIApplication {
    /**
	 * Запускает приложение.
	 *
     * @param args аргументы
     */
	public static void main(String[] args) {
		SpringApplication.run(FrontUIApplication.class, args);
	}
}
