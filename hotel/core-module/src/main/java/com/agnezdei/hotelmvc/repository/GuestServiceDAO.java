package com.agnezdei.hotelmvc.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.Query;
import org.hibernate.Session;

import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.model.GuestService;
import com.agnezdei.hotelmvc.util.HibernateUtil;

public class GuestServiceDAO extends AbstractHibernateDAO<GuestService, Long> {

    public GuestServiceDAO() {
        super();
    }
    
    public List<GuestService> findByGuestId(Long guestId) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                        "JOIN FETCH gs.service " +
                        "JOIN FETCH gs.guest " +
                        "WHERE gs.guest.id = :guestId ORDER BY gs.serviceDate";
            Query query = session.createQuery(hql);
            query.setParameter("guestId", guestId);
            @SuppressWarnings("unchecked")
            List<GuestService> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске заказов услуг гостя: " + guestId, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<GuestService> findByServiceId(Long serviceId) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                        "JOIN FETCH gs.service " +
                        "JOIN FETCH gs.guest " +
                        "WHERE gs.service.id = :serviceId ORDER BY gs.serviceDate";
            Query query = session.createQuery(hql);
            query.setParameter("serviceId", serviceId);
            @SuppressWarnings("unchecked")
            List<GuestService> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске заказов для услуги: " + serviceId, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<GuestService> findByGuestIdOrderedByPrice(Long guestId) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                        "JOIN FETCH gs.service " +
                        "JOIN FETCH gs.guest " +
                        "WHERE gs.guest.id = :guestId ORDER BY gs.service.price";
            Query query = session.createQuery(hql);
            query.setParameter("guestId", guestId);
            @SuppressWarnings("unchecked")
            List<GuestService> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске услуг гостя, отсортированных по цене: " + guestId, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<GuestService> findByGuestIdOrderedByDate(Long guestId) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                        "JOIN FETCH gs.service " +
                        "JOIN FETCH gs.guest " +
                        "WHERE gs.guest.id = :guestId ORDER BY gs.serviceDate";
            Query query = session.createQuery(hql);
            query.setParameter("guestId", guestId);
            @SuppressWarnings("unchecked")
            List<GuestService> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске услуг гостя, отсортированных по дате: " + guestId, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<GuestService> findByGuestNameOrderedByPrice(String guestName) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                        "JOIN FETCH gs.service " +
                        "JOIN FETCH gs.guest " +
                        "WHERE LOWER(gs.guest.name) LIKE LOWER(:guestName) " +
                        "ORDER BY gs.service.price";
            Query query = session.createQuery(hql);
            query.setParameter("guestName", "%" + guestName + "%");
            @SuppressWarnings("unchecked")
            List<GuestService> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске услуг гостя по имени, отсортированных по цене: " + guestName, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<GuestService> findByGuestNameOrderedByDate(String guestName) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                        "JOIN FETCH gs.service " +
                        "JOIN FETCH gs.guest " +
                        "WHERE LOWER(gs.guest.name) LIKE LOWER(:guestName) " +
                        "ORDER BY gs.serviceDate";
            Query query = session.createQuery(hql);
            query.setParameter("guestName", "%" + guestName + "%");
            @SuppressWarnings("unchecked")
            List<GuestService> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске услуг гостя по имени, отсортированных по дате: " + guestName, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public List<GuestService> findAll() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                        "JOIN FETCH gs.service " +
                        "JOIN FETCH gs.guest " +
                        "ORDER BY gs.serviceDate DESC";
            Query query = session.createQuery(hql);
            @SuppressWarnings("unchecked")
            List<GuestService> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении всех заказов услуг", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    // Переопределяем findById() с JOIN FETCH
    @Override
    public Optional<GuestService> findById(Long id) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                        "JOIN FETCH gs.service " +
                        "JOIN FETCH gs.guest " +
                        "WHERE gs.id = :id";
            Query query = session.createQuery(hql);
            query.setParameter("id", id);
            GuestService guestService = (GuestService) query.uniqueResult();
            return Optional.ofNullable(guestService);
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске заказа услуги по ID: " + id, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}