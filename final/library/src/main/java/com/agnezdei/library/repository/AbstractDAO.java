package com.agnezdei.library.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

public class AbstractDAO <T, ID> {
    @PersistenceContext
    protected EntityManager em;
    private final Class<T> entityClass;

    protected AbstractDAO(Class<T> entityClass) { this.entityClass = entityClass; }

    public T save(T entity) { em.persist(entity); return entity; }
    public T update(T entity) { return em.merge(entity); }
    public void delete(T entity) { em.remove(em.contains(entity) ? entity : em.merge(entity)); }
    public void deleteById(ID id) { findById(id).ifPresent(this::delete); }
    public Optional<T> findById(ID id) { return Optional.ofNullable(em.find(entityClass, id)); }
    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).getResultList();
    }

}
