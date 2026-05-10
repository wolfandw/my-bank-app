package io.github.wolfandw.chassis.service.impl;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.chassis.service.OutboxProcessorService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.util.UUID;

/**
 * Реализация {@link OutboxProcessorService}.
 */
public class OutboxProcessorServiceImpl implements OutboxProcessorService {
    private static final Logger LOG = LoggerFactory.getLogger(OutboxProcessorServiceImpl.class);

    private static final String NOTIFICATIONS_API_UNAVAILABLE = "Сервис нотификаций недоступен: %s";

    private final KafkaSender<UUID, Outbox> kafkaSender;
    private final String topic;
    private final OutboxRepository outboxRepository;

    /**
     * Создает сервис.
     *
     * @param kafkaSender реактивный продюсер Kafka
     * @param topic топик Kafka
     * @param outboxRepository репозиторий сообщений
     */
    public OutboxProcessorServiceImpl(KafkaSender<UUID, Outbox> kafkaSender,
                                      String topic,
                                      OutboxRepository outboxRepository) {
        this.kafkaSender = kafkaSender;
        this.topic = topic;
        this.outboxRepository = outboxRepository;
    }

    @Override
    public Flux<Outbox> processSendingUnsentOutbox() {
        return outboxRepository.findAllBySent(false).flatMap(this::sendOutbox);
    }

    @Override
    public Mono<Void> processDeletingSentOutbox() {
        return outboxRepository.deleteAllBySent(true);
    }

    private Mono<Outbox> sendOutbox(Outbox outbox) {
        LOG.debug("Outbox -> Notifications processor. Отправка запроса на нотификацию " + outbox.getMessage());
        SenderRecord<UUID, Outbox, UUID> record =
                SenderRecord.create(new ProducerRecord<>(topic, outbox.getId(), outbox), outbox.getId());
        return kafkaSender.send(Flux.just(record))
                .next()
                .flatMap(this::markSent)
                .onErrorResume(e -> {
                    LOG.error(NOTIFICATIONS_API_UNAVAILABLE.formatted(e.getMessage()), e);
                    return Mono.empty();
                });
    }

    private Mono<Outbox> markSent(SenderResult<UUID> senderResult) {
        UUID sentOutboxId = senderResult.correlationMetadata();
        LOG.debug("Notifications processor -> Outbox. Запрос на нотификацию принят");
        return outboxRepository.findById(sentOutboxId).flatMap(outbox -> {
            outbox.setSent(true);
            return outboxRepository.save(outbox);
        });
    }
}
