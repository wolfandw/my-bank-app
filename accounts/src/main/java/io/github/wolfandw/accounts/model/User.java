package io.github.wolfandw.accounts.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Класс пользователя
 */
@Table("users")
public class User {
    @Id
    private UUID id;

    @Column("login")
    private String login;

    @Column("name")
    private String name;

    @Column("birth_date")
    private LocalDate birthdate;

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Возвращает идентификатор пользователя.
     *
     * @return идентификатор пользователя
     */
    public UUID getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор пользователя.
     *
     * @param id идентификатор пользователя
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Возвращает логин пользователя.
     *
     * @return логин пользователя
     */
    public String getLogin() {
        return login;
    }

    /**
     * Устанавливает логин пользователя.
     *
     * @param login логин пользователя
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param name имя пользователя
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает дату рождения.
     *
     * @return дата рождения
     */
    public LocalDate getBirthdate() {
        return birthdate;
    }

    /**
     * Устанавливает дату рождения.
     *
     * @param birthdate дата рождения
     */
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
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
}
