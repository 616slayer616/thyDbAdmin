package org.padler.thydbadmin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;

@Service
public class DataAccessService {

    public static final String QUERY_SELECT_COUNT = "SELECT COUNT(*) AS total FROM ({}) AS query";

    @PersistenceContext
    protected EntityManager entityManager;

    private final DataSource dataSource;

    public DataAccessService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Page<Map<String, Object>> executeQuery(String sql, int page, int pageSize) {
        BigInteger countResult = countQuery(sql);

        List<Map<String, Object>> resultList = Collections.emptyList();
        try {
            resultList = jdbcQuery(sql, page, pageSize);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PageImpl<>(resultList, PageRequest.of(page, pageSize), countResult.longValue());
    }

    private List<Map<String, Object>> jdbcQuery(String sql, int page, int pageSize) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM (" + sql + ") AS query" + " LIMIT " + pageSize + " OFFSET " + page);
        ResultSetMetaData rsmd = resultSet.getMetaData();
        while (resultSet.next()) {
            Map<String, Object> result = new HashMap<>();
            for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                String columnName = rsmd.getColumnName(i);
                String columnData = resultSet.getString(i);
                result.put(columnName, columnData);
            }
            resultList.add(result);
        }
        return resultList;
    }

    private BigInteger countQuery(String sql) {
        String count = QUERY_SELECT_COUNT.replace("{}", sql);
        Query query = entityManager.createNativeQuery(count);

        List<BigInteger> resultList = query.getResultList();
        return resultList.get(0);
    }

    @Transactional
    public int executeUpdate(String sql) {
        return entityManager.createNativeQuery(sql).executeUpdate();
    }

}
