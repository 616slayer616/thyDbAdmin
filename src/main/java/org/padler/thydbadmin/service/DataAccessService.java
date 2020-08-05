package org.padler.thydbadmin.service;

import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class DataAccessService {

    @PersistenceContext
    protected EntityManager entityManager;

    public Page<Map<String, Object>> executeQuery(String sql, int page, int pageSize) {
        BigInteger countResult = countQuery(sql);
        Query query = entityManager.createNativeQuery(sql);

        org.hibernate.query.Query<Map<String, Object>> hibernateQuery = ((org.hibernate.query.Query<Map<String, Object>>) query);
        hibernateQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        hibernateQuery.setFirstResult(page * pageSize);
        hibernateQuery.setMaxResults(pageSize);

        return new PageImpl(hibernateQuery.getResultList(), PageRequest.of(page, pageSize), countResult.longValue());
    }

    private BigInteger countQuery(String sql) {
        String count = "SELECT COUNT(*) AS total FROM ({}) AS query".replace("{}", sql);
        Query query = entityManager.createNativeQuery(count);

        List<BigInteger> resultList = query.getResultList();
        return resultList.get(0);
    }

    @Transactional
    public int executeUpdate(String sql) {
        return entityManager.createNativeQuery(sql).executeUpdate();
    }

}
