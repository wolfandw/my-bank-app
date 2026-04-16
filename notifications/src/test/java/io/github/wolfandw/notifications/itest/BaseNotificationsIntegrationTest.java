package io.github.wolfandw.notifications.itest;

import io.github.wolfandw.chassis.itest.AbstractTestcontainersTest;
import io.github.wolfandw.notifications.NotificationsApplication;
import io.github.wolfandw.notifications.itest.configuration.IntegrationTestConfiguration;
import io.github.wolfandw.notifications.itest.configuration.TrxStepVerifier;
import io.github.wolfandw.notifications.repository.NotificationsRepository;
import io.github.wolfandw.notifications.service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Базовый интеграционный тест сервиса уведомлений.
 */
@ActiveProfiles("test")
@SpringBootTest(
        classes = NotificationsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.consul.enabled=false",
                "spring.cloud.consul.config.enabled=false",
                "spring.cloud.compatibility-verifier.enabled=false",
                "spring.main.allow-bean-definition-overriding=true",
                "spring.liquibase.enabled=false"
        }
)
@Import({IntegrationTestConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseNotificationsIntegrationTest extends AbstractTestcontainersTest {
    @Autowired
    protected TrxStepVerifier trxStepVerifier;

    @Autowired
    protected NotificationsService notificationsService;

    @Autowired
    protected NotificationsRepository notificationsRepository;

    @MockitoBean
    protected ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    protected ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @MockitoBean
    protected ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @DynamicPropertySource
    static void specificProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.change-log",
                () -> "classpath:db/changelog/notifications/db.changelog-master.xml");
    }
}
