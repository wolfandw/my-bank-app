package io.github.wolfandw.notifications.itest;

import io.github.wolfandw.chassis.configuration.OutboxProcessorAutoConfiguration;
import io.github.wolfandw.chassis.configuration.SecurityWebFilterConfiguration;
import io.github.wolfandw.chassis.configuration.WebClientConfiguration;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.notifications.NotificationsApplication;
import io.github.wolfandw.notifications.itest.configuration.IntegrationTestConfiguration;
import io.github.wolfandw.notifications.repository.NotificationsRepository;
import io.github.wolfandw.notifications.service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.kafka.sender.KafkaSender;

import java.util.UUID;

/**
 * Базовый интеграционный тест сервиса уведомлений.
 */
@ActiveProfiles("test")
@SpringBootTest(
        classes = NotificationsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.main.allow-bean-definition-overriding=true",
                "spring.liquibase.enabled=false",
                "spring.kafka.admin.auto-create=false",
                "spring.kafka.listener.auto-startup=false"
        }
)
@EnableAutoConfiguration(exclude = {
        SecurityWebFilterConfiguration.class,
        WebClientConfiguration.class,
        OutboxProcessorAutoConfiguration.class
})
@Import({IntegrationTestConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(topics = {"${accounts.kafka.topic}", "${cash.kafka.topic}", "${transfer.kafka.topic}"})
public abstract class BaseNotificationsIntegrationTest extends AbstractTestcontainersTest {
    @Autowired
    protected NotificationsService notificationsService;

    @Autowired
    protected NotificationsRepository notificationsRepository;

    @Autowired
    protected KafkaSender<UUID, Outbox> kafkaSenderTest;

    @Autowired
    protected EmbeddedKafkaBroker embeddedKafkaBroker;

    @DynamicPropertySource
    static void specificProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.change-log",
                () -> "classpath:db/changelog/notifications/db.changelog-master.xml");
    }
}
