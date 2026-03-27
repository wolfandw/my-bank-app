package io.github.wolfandw.chassis.repository;

import io.github.wolfandw.chassis.model.Outbox;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

/**
 * Репозиторий исходящих сообщений.
 */
public interface OutboxRepository  extends R2dbcRepository<Outbox, UUID> {
}
