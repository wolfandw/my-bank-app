package io.github.wolfandw.accounts.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Класс счета пользователя.
 */
@Table("accounts")
public class Account {
    @Id
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("balance")
    private BigDecimal balance = BigDecimal.ZERO;

    @Version
    private Long version;

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();


    /**
     * Возвращает идентификатор счета.
     *
     * @return идентификатор счета
     */
    public UUID getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор счета.
     *
     * @param id идентификатор счета
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Возвращает идентификатор пользователя.
     *
     * @return идентификатор пользователя
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * Устанавливает идентификатор пользователя.
     *
     * @param userId идентификатор пользователя
     */
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    /**
     * Возвращает баланс счета.
     *
     * @return баланс счета
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Устанавливает баланс счета.
     *
     * @param balance баланс счета
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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

    /**
     * Возвращает дату обновления.
     *
     * @return дата обновления
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Устанавливает дату обновления.
     *
     * @param updatedAt дата обновления
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Возвращает версию.
     *
     * @return версия
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Устанавливает версию.
     *
     * @param version версия
     */
    public void setVersion(Long version) {
        this.version = version;
    }
}
