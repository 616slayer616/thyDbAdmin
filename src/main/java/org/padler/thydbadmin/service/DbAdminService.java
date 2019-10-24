package org.padler.thydbadmin.service;

import org.hibernate.JDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;

@Service
public class DbAdminService {

    @PersistenceContext
    protected EntityManager entityManager;

    private final DataAccessService dataAccessService;
    private final DataSource dataSource;

    public DbAdminService(DataAccessService dataAccessService, DataSource dataSource) {
        this.dataAccessService = dataAccessService;
        this.dataSource = dataSource;
    }

    @SuppressWarnings("squid:S1168")
    public List<Map<String, Object>> executeQuery(String sql) {
        try {
            return dataAccessService.executeQuery(sql);
        } catch (Exception e) {
            dataAccessService.executeUpdate(sql);
            return null;
        }
    }

    public List<String> getTables() {
        List<String> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                result.add(tableName);
            }
        } catch (SQLException e) {
            throw new JDBCException("", e);
        }

        return result;
    }

    public List<String> getColumns(String tableName) {
        Map<Integer, String> result = new TreeMap<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, "%");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                Integer position = columns.getInt("ORDINAL_POSITION");
                result.put(position, columnName);
            }
        } catch (SQLException e) {
            throw new JDBCException("", e);
        }

        return new ArrayList<>(result.values());
    }

    public DatabaseMetaData getInfo() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.getMetaData();
        } catch (SQLException e) {
            throw new JDBCException("", e);
        }
    }

    public Page<Object[]> getData(String tableName, int page, int pageSize) {
        List<String> tables = getTables();
        Optional<String> first = tables.stream().filter(s -> s.equalsIgnoreCase(tableName)).findFirst();
        String tableNameFromDB = first.orElseThrow(() ->
                new SQLGrammarException("could not prepare statement", new SQLSyntaxErrorException("No such table " + tableName)));

        Query count = entityManager.createNativeQuery("SELECT COUNT(*) FROM " + tableNameFromDB);
        BigInteger countResult = (BigInteger) count.getSingleResult();
        Query select = entityManager.createNativeQuery("SELECT * FROM " + tableNameFromDB);
        select.setMaxResults(pageSize);
        select.setFirstResult(pageSize * page);

        return (Page<Object[]>) new PageImpl(select.getResultList(), PageRequest.of(page, pageSize), countResult.longValue());
    }

}
