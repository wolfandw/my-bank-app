package io.github.wolfandw.transfer.itest;

import io.github.wolfandw.chassis.itest.AbstractTestcontainersTest;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.transfer.TransferApplication;
import io.github.wolfandw.transfer.itest.configuration.IntegrationTestConfiguration;
import io.github.wolfandw.transfer.itest.configuration.TrxStepVerifier;
import io.github.wolfandw.transfer.service.TransferService;
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
 * Базовый интеграционный тест сервиса переводов.
 */
@ActiveProfiles("test")
@SpringBootTest(
        classes = TransferApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.consul.enabled=false",
                "spring.cloud.consul.config.enabled=false",
                "spring.cloud.compatibility-verifier.enabled=false",
                "spring.main.allow-bean-definition-overriding=true",
                "spring.liquibase.enabled=false",
                "spring.autoconfigure.exclude=io.github.wolfandw.chassis.configuration.OutboxProcessorAutoConfiguration"
        }
)
@Import({IntegrationTestConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseTransferIntegrationTest extends AbstractTestcontainersTest {
    @Autowired
    protected TrxStepVerifier trxStepVerifier;

    @Autowired
    protected TransferService transferService;

    @Autowired
    protected OutboxRepository outboxRepository;

    @MockitoBean
    protected ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    protected ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @MockitoBean
    protected ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @DynamicPropertySource
    static void specificProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.change-log",
                () -> "classpath:db/changelog/transfer/db.changelog-master.xml");
    }
}
