package com.agnezdei.library.repository;

import com.agnezdei.library.model.Catalog;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class CatalogDAO extends AbstractDAO<Catalog, Long> {

    public CatalogDAO() {
        super(Catalog.class);
    }

    public List<Catalog> findRootCatalogs() {
        return em.createQuery("SELECT c FROM Catalog c WHERE c.parent IS NULL", Catalog.class)
                .getResultList();
    }

    public List<Catalog> findChildren(Long parentId) {
        return em.createQuery("SELECT c FROM Catalog c WHERE c.parent.id = :parentId", Catalog.class)
                .setParameter("parentId", parentId)
                .getResultList();
    }
}