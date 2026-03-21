package io.github.wolfandw.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Проложение Gateway.
 */
@SpringBootApplication
public class GatewayApplication {
    /**
	 * Запускает приложение.
	 *
     * @param args аргументы
     */
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
}
