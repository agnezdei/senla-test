package com.agnezdei.hotelmvc.repository;

import com.agnezdei.hotelmvc.model.GuestService;
import org.springframework.stereotype.Repository;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class GuestServiceDAO extends AbstractDAO<GuestService, Long> {

    public GuestServiceDAO() {
        super(GuestService.class);
    }

    @Override
    public Optional<GuestService> findById(Long id) {
        String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                     "JOIN FETCH gs.service " +
                     "JOIN FETCH gs.guest " +
                     "WHERE gs.id = :id";
        TypedQuery<GuestService> query = entityManager.createQuery(hql, GuestService.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }

    @Override
    public List<GuestService> findAll() {
        String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                     "JOIN FETCH gs.service " +
                     "JOIN FETCH gs.guest " +
                     "ORDER BY gs.serviceDate DESC";
        return entityManager.createQuery(hql, GuestService.class).getResultList();
    }

    public List<GuestService> findByGuestId(Long guestId) {
        String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                     "JOIN FETCH gs.service " +
                     "JOIN FETCH gs.guest " +
                     "WHERE gs.guest.id = :guestId ORDER BY gs.serviceDate";
        return entityManager.createQuery(hql, GuestService.class)
                .setParameter("guestId", guestId)
                .getResultList();
    }

    public List<GuestService> findByServiceId(Long serviceId) {
        String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                     "JOIN FETCH gs.service " +
                     "JOIN FETCH gs.guest " +
                     "WHERE gs.service.id = :serviceId ORDER BY gs.serviceDate";
        return entityManager.createQuery(hql, GuestService.class)
                .setParameter("serviceId", serviceId)
                .getResultList();
    }

    public List<GuestService> findByGuestIdOrderedByPrice(Long guestId) {
        String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                     "JOIN FETCH gs.service " +
                     "JOIN FETCH gs.guest " +
                     "WHERE gs.guest.id = :guestId ORDER BY gs.service.price";
        return entityManager.createQuery(hql, GuestService.class)
                .setParameter("guestId", guestId)
                .getResultList();
    }

    public List<GuestService> findByGuestIdOrderedByDate(Long guestId) {
        // это то же самое, что findByGuestId, но для ясности оставим
        return findByGuestId(guestId);
    }

    public List<GuestService> findByGuestNameOrderedByPrice(String guestName) {
        String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                     "JOIN FETCH gs.service " +
                     "JOIN FETCH gs.guest " +
                     "WHERE LOWER(gs.guest.name) LIKE LOWER(:guestName) " +
                     "ORDER BY gs.service.price";
        return entityManager.createQuery(hql, GuestService.class)
                .setParameter("guestName", "%" + guestName + "%")
                .getResultList();
    }

    public List<GuestService> findByGuestNameOrderedByDate(String guestName) {
        String hql = "SELECT DISTINCT gs FROM GuestService gs " +
                     "JOIN FETCH gs.service " +
                     "JOIN FETCH gs.guest " +
                     "WHERE LOWER(gs.guest.name) LIKE LOWER(:guestName) " +
                     "ORDER BY gs.serviceDate";
        return entityManager.createQuery(hql, GuestService.class)
                .setParameter("guestName", "%" + guestName + "%")
                .getResultList();
    }
}