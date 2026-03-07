package com.agnezdei.hotelmvc.repository;

import com.agnezdei.hotelmvc.model.Guest;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class GuestDAO extends AbstractDAO<Guest, Long> {

    public GuestDAO() {
        super(Guest.class);
    }

    public Optional<Guest> findByPassportNumber(String passportNumber) {
        String hql = "SELECT g FROM Guest g WHERE g.passportNumber = :passportNumber";
        return entityManager.createQuery(hql, Guest.class)
                .setParameter("passportNumber", passportNumber)
                .getResultStream()
                .findFirst();
    }

    public List<Guest> findGuestsWithActiveBookings() {
        String hql = "SELECT DISTINCT g FROM Guest g LEFT JOIN FETCH g.bookings b WHERE b.isActive = true";
        return entityManager.createQuery(hql, Guest.class).getResultList();
    }

    public int countGuestsWithActiveBookings() {
        String hql = "SELECT COUNT(DISTINCT g.id) FROM Guest g JOIN g.bookings b WHERE b.isActive = true";
        return ((Number) entityManager.createQuery(hql).getSingleResult()).intValue();
    }
}