package com.agnezdei.hotelmvc.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.Query;
import org.hibernate.Session;

import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.util.HibernateUtil;

public class BookingDAO extends AbstractHibernateDAO<Booking, Long> {

    public BookingDAO() {
        super();
    }
    
    @Override
    public Optional<Booking> findById(Long id, Session session) throws DAOException {
        try {
            String hql = "SELECT DISTINCT b FROM Booking b " +
                        "JOIN FETCH b.guest " +
                        "JOIN FETCH b.room " +
                        "WHERE b.id = :id";
            Query query = session.createQuery(hql);
            query.setParameter("id", id);
            Booking booking = (Booking) query.uniqueResult();
            return Optional.ofNullable(booking);
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске бронирования по ID: " + id, e);
        }
    }
    
    @Override
    public List<Booking> findAll(Session session) throws DAOException {
        try {
            String hql = "SELECT DISTINCT b FROM Booking b " +
                        "JOIN FETCH b.guest " +
                        "JOIN FETCH b.room " +
                        "ORDER BY b.checkInDate DESC";
            Query query = session.createQuery(hql);
            @SuppressWarnings("unchecked")
            List<Booking> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении всех бронирований", e);
        }
    }
    
    public List<Booking> findByRoomId(Long roomId, Session session) throws DAOException {
        try {
            String hql = "SELECT DISTINCT b FROM Booking b " +
                        "JOIN FETCH b.guest " +
                        "JOIN FETCH b.room " +
                        "WHERE b.room.id = :roomId ORDER BY b.checkInDate DESC";
            Query query = session.createQuery(hql);
            query.setParameter("roomId", roomId);
            @SuppressWarnings("unchecked")
            List<Booking> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске бронирований комнаты: " + roomId, e);
        }
    }
    
    public List<Booking> findActiveBookings(Session session) throws DAOException {
        try {
            String hql = "SELECT DISTINCT b FROM Booking b " +
                        "JOIN FETCH b.guest " +
                        "JOIN FETCH b.room " +
                        "WHERE b.isActive = true ORDER BY b.checkInDate";
            Query query = session.createQuery(hql);
            @SuppressWarnings("unchecked")
            List<Booking> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске активных бронирований", e);
        }
    }

    
    public List<Booking> findByRoomId(Long roomId) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            return findByRoomId(roomId, session);
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске бронирований комнаты: " + roomId, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Booking> findActiveBookings() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            return findActiveBookings(session);
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске активных бронирований", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Booking> findActiveBookingsOrderedByGuestName() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT b FROM Booking b " +
                        "JOIN FETCH b.guest " +
                        "JOIN FETCH b.room " +
                        "WHERE b.isActive = true ORDER BY b.guest.name";
            Query query = session.createQuery(hql);
            @SuppressWarnings("unchecked")
            List<Booking> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске активных бронирований, отсортированных по имени гостя", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Booking> findActiveBookingsOrderedByCheckoutDate() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT b FROM Booking b " +
                        "JOIN FETCH b.guest " +
                        "JOIN FETCH b.room " +
                        "WHERE b.isActive = true ORDER BY b.checkOutDate";
            Query query = session.createQuery(hql);
            @SuppressWarnings("unchecked")
            List<Booking> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске активных бронирований, отсортированных по дате выезда", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Booking> findLastThreeGuestsByRoomId(Long roomId) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT b FROM Booking b " +
                        "JOIN FETCH b.guest " +
                        "WHERE b.room.id = :roomId AND b.isActive = false " +
                        "ORDER BY b.checkOutDate DESC";
            Query query = session.createQuery(hql);
            query.setParameter("roomId", roomId);
            query.setMaxResults(3);
            @SuppressWarnings("unchecked")
            List<Booking> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске истории комнаты: " + roomId, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<Booking> findAll() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT b FROM Booking b " +
                        "JOIN FETCH b.guest " +
                        "JOIN FETCH b.room " +
                        "ORDER BY b.checkInDate DESC";
            Query query = session.createQuery(hql);
            @SuppressWarnings("unchecked")
            List<Booking> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении всех бронирований", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}