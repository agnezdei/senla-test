package com.agnezdei.hotelmvc.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.Query;
import org.hibernate.Session;

import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.util.HibernateUtil;

public class ServiceDAO extends AbstractHibernateDAO<Service, Long> {

    public ServiceDAO() {
        super();
    }
    
    public Optional<Service> findByName(String name, Session session) throws DAOException {
        try {
            String hql = "FROM Service s WHERE s.name = :name";
            Query query = session.createQuery(hql);
            query.setParameter("name", name);
            Service service = (Service) query.uniqueResult();
            return Optional.ofNullable(service);
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске услуги по названию: " + name, e);
        }
    }
    
    public List<Service> findByCategory(ServiceCategory category, Session session) throws DAOException {
        try {
            String hql = "FROM Service s WHERE s.category = :category ORDER BY s.name";
            Query query = session.createQuery(hql);
            query.setParameter("category", category);
            @SuppressWarnings("unchecked")
            List<Service> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске услуг по категории: " + category, e);
        }
    }
    
    public List<Service> findAllOrderedByCategoryAndPrice(Session session) throws DAOException {
        try {
            String hql = "FROM Service s ORDER BY s.category, s.price";
            Query query = session.createQuery(hql);
            @SuppressWarnings("unchecked")
            List<Service> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении услуг, отсортированных по категории и цене", e);
        }
    }
    
    
    public Optional<Service> findByName(String name) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            return findByName(name, session);
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске услуги по названию: " + name, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Service> findByCategory(ServiceCategory category) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            return findByCategory(category, session);
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске услуг по категории: " + category, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Service> findAllOrderedByCategoryAndPrice() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            return findAllOrderedByCategoryAndPrice(session);
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении услуг, отсортированных по категории и цене", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}