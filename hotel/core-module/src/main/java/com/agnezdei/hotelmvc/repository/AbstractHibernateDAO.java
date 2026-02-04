package com.agnezdei.hotelmvc.repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.util.HibernateUtil;

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
    public T save(T entity) throws DAOException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DAOException("Ошибка сохранения " + entityClass.getSimpleName(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    @Override
    public Optional<T> findById(ID id) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            T entity = (T) session.get(entityClass, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new DAOException("Ошибка поиска по ID: " + id, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    @Override
    public List<T> findAll() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            Query query = session.createQuery("FROM " + entityClass.getSimpleName());
            @SuppressWarnings("unchecked")
            List<T> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка получения всех записей", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    @Override
    public void update(T entity) throws DAOException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DAOException("Ошибка обновления " + entityClass.getSimpleName(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    @Override
    public void delete(ID id) throws DAOException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();
            
            T entity = (T) session.get(entityClass, id);
            if (entity != null) {
                session.delete(entity);
            }
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DAOException("Ошибка удаления " + entityClass.getSimpleName() + " с ID=" + id, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}