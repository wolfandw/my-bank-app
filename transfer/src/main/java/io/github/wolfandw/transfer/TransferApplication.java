package io.github.wolfandw.transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Проложение Transfer.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class TransferApplication {
    /**
	 * Запускает приложение.
	 *
     * @param args аргументы
     */
	public static void main(String[] args) {
		SpringApplication.run(TransferApplication.class, args);
	}
}
