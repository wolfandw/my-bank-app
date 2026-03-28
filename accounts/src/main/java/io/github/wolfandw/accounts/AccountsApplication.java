package io.github.wolfandw.accounts;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

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


	/**
	 * WebMvcConfigurer с настроенным CORS.
	 *
	 * @return WebMvcConfigurer с настроенным CORS
	 */
	@Bean
	public WebFluxConfigurer corsConfigurer() {
		return new WebFluxConfigurer() {
			@Override
			public void addCorsMappings(@NonNull CorsRegistry registry) {
				registry.addMapping("/api/**")
						.allowedOriginPatterns("http://localhost")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true)
						.maxAge(3600);
			}
		};
	}
}
