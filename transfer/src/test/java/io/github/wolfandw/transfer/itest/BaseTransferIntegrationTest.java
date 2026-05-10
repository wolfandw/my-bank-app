package io.github.wolfandw.transfer.itest;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.chassis.service.OutboxProcessorService;
import io.github.wolfandw.transfer.TransferApplication;
import io.github.wolfandw.transfer.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.kafka.sender.KafkaSender;

import java.util.UUID;

/**
 * Базовый интеграционный тест сервиса переводов.
 */
@ActiveProfiles("test")
@SpringBootTest(
        classes = TransferApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.compatibility-verifier.enabled=false",
                "spring.main.allow-bean-definition-overriding=true",
                "spring.liquibase.enabled=false"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(topics = {"${spring.kafka.topics.topic}"})
public abstract class BaseTransferIntegrationTest extends AbstractTestcontainersTest {
    @Autowired
    protected TransferService transferService;

    @Autowired
    protected OutboxRepository outboxRepository;

    @Autowired
    protected OutboxProcessorService outboxProcessorService;

    @Autowired
    protected KafkaSender<UUID, Outbox> kafkaSender;

    @MockitoBean
    protected ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    protected ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @MockitoBean
    protected ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @Value("${spring.kafka.topics.topic}")
    protected String transferTopic;

    @DynamicPropertySource
    static void specificProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.change-log",
                () -> "classpath:db/changelog/transfer/db.changelog-master.xml");
    }
}
