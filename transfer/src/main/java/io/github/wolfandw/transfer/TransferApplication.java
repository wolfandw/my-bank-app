package io.github.wolfandw.transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Проложение Transfer.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableR2dbcRepositories(basePackages = {"io.github.wolfandw.chassis.repository", "io.github.wolfandw.transfer.repository"})
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
