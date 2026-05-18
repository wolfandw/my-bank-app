package io.github.wolfandw.notifications.itest;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.notifications.model.Notification;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Абстрактный класс для тестов с Testcontainers.
 */
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractTestcontainersTest {

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:17-alpine")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass")
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> String.format("r2dbc:postgresql://%s:%d/%s",
                postgreSQLContainer.getHost(),
                postgreSQLContainer.getMappedPort(5432),
                postgreSQLContainer.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);

        registry.add("spring.liquibase.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.liquibase.user", postgreSQLContainer::getUsername);
        registry.add("spring.liquibase.password", postgreSQLContainer::getPassword);
        registry.add("spring.liquibase.contexts",  () -> "dev");
        registry.add("spring.liquibase.enabled", () -> "true");
    }

    protected List<Outbox> getTestOutboxes() {
        List<Outbox> testOutboxes = new ArrayList<>(getOutboxes(true));
        testOutboxes.addAll(getOutboxes(false));
        return testOutboxes;
    }

    protected List<Notification> getTestNotifications() {
        List<Notification> testNotifications = new ArrayList<>(getNotifications(true));
        testNotifications.addAll(getNotifications(false));
        return testNotifications;
    }

    private List<Notification> getNotifications(boolean sent) {
        return IntStream.range(0, 3).mapToObj(i -> {
            Notification notification = new Notification();
            notification.setUserId("user");
            notification.setOutboxId(UUID.randomUUID());
            notification.setMessage("test message " + i);
            notification.setSent(sent);
            return notification;
        }).toList();
    }

    private List<Outbox> getOutboxes(boolean sent) {
        return IntStream.range(0, 3).mapToObj(i -> {
            Outbox outbox = new Outbox();
            outbox.setUserId("user");
            outbox.setMessage("test message " + i);
            outbox.setSent(sent);
            return outbox;
        }).toList();
    }
}
