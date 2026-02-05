package com.agnezdei.hotelmvc.repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import org.hibernate.Query;
import org.hibernate.Session;
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
            session.save(entity);
            return entity;
        } catch (Exception e) {
            throw new DAOException("Ошибка сохранения " + entityClass.getSimpleName(), e);
        }
    }
    
    @Override
    public Optional<T> findById(ID id, Session session) throws DAOException {
        try {
            T entity = (T) session.get(entityClass, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new DAOException("Ошибка поиска по ID: " + id, e);
        }
    }
    
    @Override
    public List<T> findAll(Session session) throws DAOException {
        try {
            Query query = session.createQuery("FROM " + entityClass.getSimpleName());
            @SuppressWarnings("unchecked")
            List<T> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка получения всех записей", e);
        }
    }
    
    @Override
    public void update(T entity, Session session) throws DAOException {
        try {
            session.update(entity);
        } catch (Exception e) {
            throw new DAOException("Ошибка обновления " + entityClass.getSimpleName(), e);
        }
    }
    
    @Override
    public void delete(ID id, Session session) throws DAOException {
        try {
            T entity = (T) session.get(entityClass, id);
            if (entity != null) {
                session.delete(entity);
            }
        } catch (Exception e) {
            throw new DAOException("Ошибка удаления " + entityClass.getSimpleName() + " с ID=" + id, e);
        }
    }
}