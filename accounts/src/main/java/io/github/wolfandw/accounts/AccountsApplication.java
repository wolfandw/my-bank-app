package io.github.wolfandw.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Проложение аккаунтов.
 */
@SpringBootApplication
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
