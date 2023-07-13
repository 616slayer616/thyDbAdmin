package org.padler.thydbadmin.service;

import org.hibernate.JDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataAccessService {

    private static final Logger logger = LoggerFactory.getLogger(DataAccessService.class);

    public static final String QUERY_SELECT_COUNT = "SELECT COUNT(*) AS total FROM ({}) AS query";
    public static final String DATABASE_PRODUCT_NAME_PG = "PostgreSQL";
    public static final String DATABASE_PRODUCT_NAME_MY = "MySQL";
    public static final String DATABASE_PRODUCT_NAME_H2 = "H2";
    public static final String DATABASE_PRODUCT_NAME_DB2 = "DB2";
    public static final String QUERY_SELECT_ALL = "SELECT * FROM ";
    public static final String QUERY_LIMIT = " LIMIT ";

    @PersistenceContext
    protected EntityManager entityManager;

    private final DataSource dataSource;

    public DataAccessService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DatabaseMetaData getInfo() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.getMetaData();
        } catch (SQLException e) {
            throw new JDBCException("", e);
        }
    }

    public Page<Map<String, Object>> executeQuery(String sql, int page, int pageSize) {
        BigInteger countResult = countQuery(sql);

        List<Map<String, Object>> resultList = jdbcQuery(sql, page, pageSize);

        if (resultList.size() > pageSize) {
            return new PageImpl<>(resultList, PageRequest.of(0, resultList.size()), countResult.longValue());
        } else {
            return new PageImpl<>(resultList, PageRequest.of(page, pageSize), countResult.longValue());
        }
    }

    private String createPaging(String sql, int page, int pageSize) {
        DatabaseMetaData info = getInfo();
        String dbVendor = "";
        int offset = page * pageSize;
        try {
            dbVendor = info.getDatabaseProductName();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        switch (dbVendor) {
            case DATABASE_PRODUCT_NAME_PG:
                sql = QUERY_SELECT_ALL + " (" + sql + ") AS query" + QUERY_LIMIT + pageSize + " OFFSET " + offset;
                break;
            case DATABASE_PRODUCT_NAME_DB2:
            case DATABASE_PRODUCT_NAME_H2:
            case DATABASE_PRODUCT_NAME_MY:
                sql = QUERY_SELECT_ALL + " (" + sql + ") AS query" + QUERY_LIMIT + pageSize + ", " + offset;
                break;
            default:
        }
        return sql;
    }

    private List<Map<String, Object>> jdbcQuery(String sql, int page, int pageSize) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(createPaging(sql, page, pageSize))) {
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
        } catch (SQLException e) {
            logger.error(e.getMessage());
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
    @SuppressWarnings("javasecurity:S3649")
    public int executeUpdate(String sql) {
        return entityManager.createNativeQuery(sql).executeUpdate();
    }

}
