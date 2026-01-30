package com.agnezdei.hotelmvc.repository.dao;

import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.exceptions.DAOException;

public interface GenericDAO<T, ID> {
    T save(T entity) throws DAOException;

    Optional<T> findById(ID id) throws DAOException;

    List<T> findAll() throws DAOException;

    void update(T entity) throws DAOException;

    void delete(ID id) throws DAOException;
}