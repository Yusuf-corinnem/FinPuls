package com.finpuls.service.common;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractCrudService<E, ID> implements CrudService<E, ID> {

    private final JpaRepository<E, ID> repository;
    private final Class<E> entityClass;

    protected AbstractCrudService(Class<E> entityClass, JpaRepository<E, ID> repository) {
        this.entityClass = entityClass;
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<E> findById(ID id) {
        requireNonNull(id, "id");
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<E> findAll() {
        return repository.findAll();
    }

    @Transactional
    public E save(E entity) {
        requireNonNull(entity, "entity");
        return repository.save(entity);
    }

    @Transactional
    public void deleteById(ID id) {
        requireNonNull(id, "id");
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Сущность не найдена по id: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<E> findByField(String fieldName, Object value) {
        E probe = buildProbe(fieldName, value);
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)
                .withIgnoreCase();
        return repository.findOne(Example.of(probe, matcher));
    }

    @Transactional(readOnly = true)
    public List<E> findAllByField(String fieldName, Object value) {
        E probe = buildProbe(fieldName, value);
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)
                .withIgnoreCase();
        return repository.findAll(Example.of(probe, matcher));
    }

    private E buildProbe(String fieldName, Object value) {
        try {
            E instance = entityClass.getDeclaredConstructor().newInstance();
            Field field = entityClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
            return instance;
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось создать образец по полю '" + fieldName + "': " + e.getMessage(), e);
        }
    }

    // ===== Helpers (null/empty checks, existence, required entity) =====

    protected <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " не должен быть null");
        }
        return value;
    }

    protected String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " не должен быть пустым");
        }
        return value;
    }

    protected E getOrThrow(Optional<E> optional, Supplier<String> messageSupplier) {
        return optional.orElseThrow(() -> new IllegalArgumentException(messageSupplier.get()));
    }

    protected boolean existsByField(String fieldName, Object value) {
        return findByField(fieldName, value).isPresent();
    }

    // ===== ID-based helpers =====
    @Transactional(readOnly = true)
    public boolean existsById(ID id) {
        requireNonNull(id, "id");
        return repository.existsById(id);
    }

    @Transactional(readOnly = true)
    public E getRequiredById(ID id) {
        return getOrThrow(findById(id), () -> "Сущность не найдена по id: " + id);
    }

    // ===== Partial update helpers =====
    protected <T> void setIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    protected void setIfNotBlank(String value, Consumer<String> setter) {
        if (value != null && !value.isBlank()) {
            setter.accept(value);
        }
    }
}