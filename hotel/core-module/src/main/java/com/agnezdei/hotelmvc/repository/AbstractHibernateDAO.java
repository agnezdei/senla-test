package com.agnezdei.hotelmvc.repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import  jakarta.persistence.Query;

import com.agnezdei.hotelmvc.exceptions.DAOException;

public abstract class AbstractHibernateDAO<T, ID extends Serializable> implements GenericDAO<T, ID> {
    
    private Class<T> entityClass;
    
    @SuppressWarnings("unchecked")
    public AbstractHibernateDAO() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    protected Class<T> getEntityClass() {
        return entityClass;
    }
    
    @Override
    public T save(T entity, Session session) throws DAOException {
        try {
            session.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new DAOException("Ошибка сохранения " + entityClass.getSimpleName(), e);
        }
    }
    
    @Override
    public Optional<T> findById(ID id, Session session) throws DAOException {
        try {
            T entity = session.get(entityClass, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new DAOException("Ошибка поиска по ID: " + id, e);
        }
    }
    
    @Override
    public List<T> findAll(Session session) throws DAOException {
        try {
        @SuppressWarnings("unchecked")
        List<T> result = session.createQuery(
            "FROM " + entityClass.getSimpleName()
        ).list();
        
        return result;
    } catch (Exception e) {
        throw new DAOException("Ошибка получения всех записей", e);
    }
    }
    
    @Override
    public void update(T entity, Session session) throws DAOException {
        try {
            session.merge(entity);
        } catch (Exception e) {
            throw new DAOException("Ошибка обновления " + entityClass.getSimpleName(), e);
        }
    }
    
    @Override
    public void delete(ID id, Session session) throws DAOException {
        try {
            T entity = session.get(entityClass, id);
            if (entity != null) {
                session.remove(entity);
            }
        } catch (Exception e) {
            throw new DAOException("Ошибка удаления " + entityClass.getSimpleName() + " с ID=" + id, e);
        }
    }
}