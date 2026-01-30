package com.agnezdei.hotelmvc.repository.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.hibernate.Query;
import org.hibernate.Session;

import com.agnezdei.hotelmvc.entity.Room;
import com.agnezdei.hotelmvc.entity.RoomStatus;
import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.util.HibernateUtil;

public class RoomDAO extends AbstractHibernateDAO<Room, Long> {

    public RoomDAO() {
        super();
    }
    
    public Optional<Room> findByNumber(String number) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "FROM Room r WHERE r.number = :number";
            Query query = session.createQuery(hql);
            query.setParameter("number", number);
            Room room = (Room) query.uniqueResult();
            return Optional.ofNullable(room);
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске комнаты по номеру: " + number, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Room> findAvailableRooms() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "FROM Room r WHERE r.status = :status ORDER BY r.number";
            Query query = session.createQuery(hql);
            query.setParameter("status", RoomStatus.AVAILABLE);
            @SuppressWarnings("unchecked")
            List<Room> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске доступных комнат", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Room> findAllOrderedByPrice() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "FROM Room r ORDER BY r.price";
            Query query = session.createQuery(hql);
            @SuppressWarnings("unchecked")
            List<Room> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении комнат, отсортированных по цене", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Room> findAllOrderedByCapacity() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "FROM Room r ORDER BY r.capacity";
            Query query = session.createQuery(hql);
            @SuppressWarnings("unchecked")
            List<Room> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении комнат, отсортированных по вместимости", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Room> findAllOrderedByStars() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "FROM Room r ORDER BY r.stars";
            Query query = session.createQuery(hql);
            @SuppressWarnings("unchecked")
            List<Room> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении комнат, отсортированных по звездам", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Room> findAvailableRoomsOrderedByPrice() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "FROM Room r WHERE r.status = :status ORDER BY r.price";
            Query query = session.createQuery(hql);
            query.setParameter("status", RoomStatus.AVAILABLE);
            @SuppressWarnings("unchecked")
            List<Room> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении доступных комнат, отсортированных по цене", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Room> findAvailableRoomsOrderedByCapacity() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "FROM Room r WHERE r.status = :status ORDER BY r.capacity";
            Query query = session.createQuery(hql);
            query.setParameter("status", RoomStatus.AVAILABLE);
            @SuppressWarnings("unchecked")
            List<Room> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении доступных комнат, отсортированных по вместимости", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Room> findAvailableRoomsOrderedByStars() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "FROM Room r WHERE r.status = :status ORDER BY r.stars";
            Query query = session.createQuery(hql);
            query.setParameter("status", RoomStatus.AVAILABLE);
            @SuppressWarnings("unchecked")
            List<Room> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении доступных комнат, отсортированных по звездам", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Room> findAllOrderedByTypeAndPrice() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "FROM Room r ORDER BY r.type, r.price";
            Query query = session.createQuery(hql);
            @SuppressWarnings("unchecked")
            List<Room> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при получении комнат, отсортированных по типу и цене", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public List<Room> findRoomsAvailableOnDate(LocalDate date) throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "FROM Room r WHERE r.status = :status " +
                        "AND r.id NOT IN (" +
                        "  SELECT b.room.id FROM Booking b " +
                        "  WHERE b.isActive = true " +
                        "  AND :date BETWEEN b.checkInDate AND b.checkOutDate" +
                        ") ORDER BY r.number";
            
            Query query = session.createQuery(hql);
            query.setParameter("status", RoomStatus.AVAILABLE);
            query.setParameter("date", date.toString());
            
            @SuppressWarnings("unchecked")
            List<Room> result = query.list();
            return result;
        } catch (Exception e) {
            throw new DAOException("Ошибка при поиске комнат на дату: " + date, e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public int countAvailableRooms() throws DAOException {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            String hql = "SELECT COUNT(r.id) FROM Room r WHERE r.status = :status";
            Query query = session.createQuery(hql);
            query.setParameter("status", RoomStatus.AVAILABLE);
            Long count = (Long) query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            throw new DAOException("Ошибка при подсчете доступных комнат", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}