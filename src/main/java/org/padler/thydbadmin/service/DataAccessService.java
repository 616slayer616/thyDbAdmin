package org.padler.thydbadmin.service;

import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@Service
public class DataAccessService {

    @PersistenceContext
    protected EntityManager entityManager;

    public List<Map<String, Object>> executeQuery(String sql) {
        Query query = entityManager.createNativeQuery(sql);

        org.hibernate.query.Query<Map<String, Object>> hibernateQuery = ((org.hibernate.query.Query<Map<String, Object>>) query);
        hibernateQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        return hibernateQuery.getResultList();
    }

    @Transactional
    public int executeUpdate(String sql) {
        return entityManager.createNativeQuery(sql).executeUpdate();
    }

}
