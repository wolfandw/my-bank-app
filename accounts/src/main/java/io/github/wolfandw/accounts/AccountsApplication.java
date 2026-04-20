package io.github.wolfandw.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Проложение аккаунтов.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableR2dbcRepositories(basePackages = {"io.github.wolfandw.chassis.repository", "io.github.wolfandw.accounts.repository"})
public class AccountsApplication {
    /**
	 * Запускает приложение.
	 *
     * @param args аргументы
     */
	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}
}
