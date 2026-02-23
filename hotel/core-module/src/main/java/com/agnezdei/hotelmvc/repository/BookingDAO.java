package com.agnezdei.hotelmvc.repository;

import com.agnezdei.hotelmvc.model.Booking;
import org.springframework.stereotype.Repository;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingDAO extends AbstractDAO<Booking, Long> {

    public BookingDAO() {
        super(Booking.class);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        String hql = "SELECT DISTINCT b FROM Booking b JOIN FETCH b.guest JOIN FETCH b.room WHERE b.id = :id";
        return entityManager.createQuery(hql, Booking.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Booking> findAll() {
        String hql = "SELECT DISTINCT b FROM Booking b JOIN FETCH b.guest JOIN FETCH b.room ORDER BY b.checkInDate DESC";
        return entityManager.createQuery(hql, Booking.class).getResultList();
    }

    public List<Booking> findByRoomId(Long roomId) {
        String hql = "SELECT DISTINCT b FROM Booking b " +
                     "JOIN FETCH b.guest " +
                     "JOIN FETCH b.room " +
                     "WHERE b.room.id = :roomId ORDER BY b.checkInDate DESC";
        return entityManager.createQuery(hql, Booking.class)
                .setParameter("roomId", roomId)
                .getResultList();
    }

    public List<Booking> findActiveBookings() {
        String hql = "SELECT DISTINCT b FROM Booking b " +
                     "JOIN FETCH b.guest " +
                     "JOIN FETCH b.room " +
                     "WHERE b.isActive = true ORDER BY b.checkInDate";
        return entityManager.createQuery(hql, Booking.class).getResultList();
    }

    public List<Booking> findActiveBookingsOrderedByGuestName() {
        String hql = "SELECT DISTINCT b FROM Booking b " +
                     "JOIN FETCH b.guest " +
                     "JOIN FETCH b.room " +
                     "WHERE b.isActive = true ORDER BY b.guest.name";
        return entityManager.createQuery(hql, Booking.class).getResultList();
    }

    public List<Booking> findActiveBookingsOrderedByCheckoutDate() {
        String hql = "SELECT DISTINCT b FROM Booking b " +
                     "JOIN FETCH b.guest " +
                     "JOIN FETCH b.room " +
                     "WHERE b.isActive = true ORDER BY b.checkOutDate";
        return entityManager.createQuery(hql, Booking.class).getResultList();
    }

    public List<Booking> findLastThreeGuestsByRoomId(Long roomId) {
        String jpql = "SELECT DISTINCT b FROM Booking b " +
                      "JOIN FETCH b.guest " +
                      "WHERE b.room.id = :roomId AND b.isActive = false " +
                      "ORDER BY b.checkOutDate DESC";
        return entityManager.createQuery(jpql, Booking.class)
                .setParameter("roomId", roomId)
                .setMaxResults(3)
                .getResultList();
    }
}