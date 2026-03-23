package io.github.wolfandw.cash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Проложение Cash.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CashApplication {
    /**
	 * Запускает приложение.
	 *
     * @param args аргументы
     */
	public static void main(String[] args) {
		SpringApplication.run(CashApplication.class, args);
	}
}
