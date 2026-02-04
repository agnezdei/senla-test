package com.agnezdei.hotelmvc.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.Query;
import org.hibernate.Session;

import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.util.HibernateUtil;

public class GuestDAO extends AbstractHibernateDAO<Guest, Long> {

    public GuestDAO() {
        super();
    }
    
    public Optional<Guest> findByPassportNumber(String passportNumber) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "FROM Guest WHERE passportNumber = :passportNumber";
            Query query = session.createQuery(hql);
            query.setParameter("passportNumber", passportNumber);
            Guest guest = (Guest) query.uniqueResult();
            return Optional.ofNullable(guest);
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске гостя по номеру паспорта: " + passportNumber, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<Guest> findGuestsWithActiveBookings() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT DISTINCT g FROM Guest g " +
                        "LEFT JOIN FETCH g.bookings b " +
                        "WHERE b.isActive = true";
            Query query = session.createQuery(hql);
            @SuppressWarnings("unchecked")
            List<Guest> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении гостей с активными бронированиями", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public int countGuestsWithActiveBookings() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT COUNT(DISTINCT g.id) FROM Guest g " +
                        "JOIN g.bookings b " +
                        "WHERE b.isActive = true";
            Query query = session.createQuery(hql);
            Long count = (Long) query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            throw new DAOException("Ошибка при подсчете активных гостей", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}