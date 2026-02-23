package com.agnezdei.hotelmvc.repository;

import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;
import org.springframework.stereotype.Repository;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class RoomDAO extends AbstractDAO<Room, Long> {

    public RoomDAO() {
        super(Room.class);
    }

    // Переопределять findById не обязательно, если не нужно fetch, но можно оставить базовый

    @Override
    public List<Room> findAll() {
        String hql = "FROM Room r ORDER BY r.number";
        return entityManager.createQuery(hql, Room.class).getResultList();
    }

    public Optional<Room> findByNumber(String number) {
        String hql = "FROM Room r WHERE r.number = :number";
        TypedQuery<Room> query = entityManager.createQuery(hql, Room.class);
        query.setParameter("number", number);
        return query.getResultStream().findFirst();
    }

    public List<Room> findAvailableRooms() {
        String hql = "FROM Room r WHERE r.status = :status ORDER BY r.number";
        return entityManager.createQuery(hql, Room.class)
                .setParameter("status", RoomStatus.AVAILABLE)
                .getResultList();
    }

    public List<Room> findRoomsAvailableOnDate(LocalDate date) {
        String hql = "FROM Room r WHERE r.status = :status " +
                     "AND r.id NOT IN (" +
                     "  SELECT b.room.id FROM Booking b " +
                     "  WHERE b.isActive = true " +
                     "  AND :date BETWEEN b.checkInDate AND b.checkOutDate" +
                     ") ORDER BY r.number";
        return entityManager.createQuery(hql, Room.class)
                .setParameter("status", RoomStatus.AVAILABLE)
                .setParameter("date", date)
                .getResultList();
    }

    public int countAvailableRooms() {
        String hql = "SELECT COUNT(r.id) FROM Room r WHERE r.status = :status";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("status", RoomStatus.AVAILABLE)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    public List<Room> findAllOrderedByPrice() {
        String hql = "FROM Room r ORDER BY r.price";
        return entityManager.createQuery(hql, Room.class).getResultList();
    }

    public List<Room> findAllOrderedByCapacity() {
        String hql = "FROM Room r ORDER BY r.capacity";
        return entityManager.createQuery(hql, Room.class).getResultList();
    }

    public List<Room> findAllOrderedByStars() {
        String hql = "FROM Room r ORDER BY r.stars";
        return entityManager.createQuery(hql, Room.class).getResultList();
    }

    public List<Room> findAvailableRoomsOrderedByPrice() {
        String hql = "FROM Room r WHERE r.status = :status ORDER BY r.price";
        return entityManager.createQuery(hql, Room.class)
                .setParameter("status", RoomStatus.AVAILABLE)
                .getResultList();
    }

    public List<Room> findAvailableRoomsOrderedByCapacity() {
        String hql = "FROM Room r WHERE r.status = :status ORDER BY r.capacity";
        return entityManager.createQuery(hql, Room.class)
                .setParameter("status", RoomStatus.AVAILABLE)
                .getResultList();
    }

    public List<Room> findAvailableRoomsOrderedByStars() {
        String hql = "FROM Room r WHERE r.status = :status ORDER BY r.stars";
        return entityManager.createQuery(hql, Room.class)
                .setParameter("status", RoomStatus.AVAILABLE)
                .getResultList();
    }

    public List<Room> findAllOrderedByTypeAndPrice() {
        String hql = "FROM Room r ORDER BY r.type, r.price";
        return entityManager.createQuery(hql, Room.class).getResultList();
    }
}