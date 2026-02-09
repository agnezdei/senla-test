package com.agnezdei.hotelmvc.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;

import com.agnezdei.hotelmvc.exceptions.DAOException;

public interface GenericDAO<T, ID> {
    T save(T entity, Session session) throws DAOException;
    Optional<T> findById(ID id, Session session) throws DAOException;
    List<T> findAll(Session session) throws DAOException;
    void update(T entity, Session session) throws DAOException;
    void delete(ID id, Session session) throws DAOException;
}