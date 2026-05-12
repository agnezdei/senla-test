package com.agnezdei.library.repository;

import com.agnezdei.library.model.User;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UserDAO extends AbstractDAO<User, Long> {

    public UserDAO() {
        super(User.class);
    }

    public Optional<User> findByUsername(String username) {
        return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }
}