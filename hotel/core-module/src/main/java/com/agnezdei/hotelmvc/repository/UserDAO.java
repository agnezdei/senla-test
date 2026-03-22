package com.agnezdei.hotelmvc.repository;

import com.agnezdei.hotelmvc.model.User;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UserDAO extends AbstractDAO<User, Long> {

    public UserDAO() {
        super(User.class);
    }

    public Optional<User> findByUsername(String username) {
        String hql = "FROM User s WHERE s.username = :username";
        TypedQuery<User> query = entityManager.createQuery(hql, User.class);
        query.setParameter("username", username);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}