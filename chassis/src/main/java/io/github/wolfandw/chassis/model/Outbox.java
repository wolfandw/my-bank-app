package io.github.wolfandw.chassis.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Класс модели исходящего сообщения.
 */
@Table(name = "outbox")
public class Outbox {
    @Id
    private UUID id;

    @Column("user_login")
    private String userLogin;

    @Column("message")
    private String message;

    @Column("sent")
    private Boolean sent;

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Возвращает идентификатор сообщения.
     *
     * @return идентификатор сообщения
     */
    public UUID getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор сообщения.
     *
     * @param id идентификатор сообщения
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Возвращает логин пользователя.
     *
     * @return логин пользователя
     */
    public String getUserLogin() {
        return userLogin;
    }

    /**
     * Устанавливает логин пользователя.
     *
     * @param userLogin логин пользователя
     */
    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    /**
     * Возвращает текст сообщения.
     *
     * @return текст сообщения
     */
    public String getMessage() {
        return message;
    }

    /**
     * Устанавливает текст сообщения.
     *
     * @param message текст сообщения
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Возвращает отправлено.
     *
     * @return отправлено
     */
    public Boolean getSent() {
        return sent;
    }

    /**
     * Устанавливает отправлено.
     *
     * @param sent отправлено.
     */
    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    /**
     * Возвращает дату создания.
     *
     * @return дата создания
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Устанавливает дату создания.
     *
     * @param createdAt дата создания
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
