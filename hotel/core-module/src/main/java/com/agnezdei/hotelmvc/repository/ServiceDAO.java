package com.agnezdei.hotelmvc.repository;

import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import org.springframework.stereotype.Repository;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class ServiceDAO extends AbstractDAO<Service, Long> {

    public ServiceDAO() {
        super(Service.class);
    }

    @Override
    public List<Service> findAll() {
        String hql = "FROM Service s ORDER BY s.name";
        return entityManager.createQuery(hql, Service.class).getResultList();
    }

    public Optional<Service> findByName(String name) {
        String hql = "FROM Service s WHERE s.name = :name";
        TypedQuery<Service> query = entityManager.createQuery(hql, Service.class);
        query.setParameter("name", name);
        return query.getResultStream().findFirst();
    }

    public List<Service> findByCategory(ServiceCategory category) {
        String hql = "FROM Service s WHERE s.category = :category ORDER BY s.name";
        return entityManager.createQuery(hql, Service.class)
                .setParameter("category", category)
                .getResultList();
    }

    public List<Service> findAllOrderedByCategoryAndPrice() {
        String hql = "FROM Service s ORDER BY s.category, s.price";
        return entityManager.createQuery(hql, Service.class).getResultList();
    }
}