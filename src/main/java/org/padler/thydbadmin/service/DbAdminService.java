package org.padler.thydbadmin.service;

import org.hibernate.JDBCException;
import org.hibernate.MappingException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import static org.padler.thydbadmin.service.DataAccessService.QUERY_SELECT_ALL;

@Service
public class DbAdminService {

    private final DataAccessService dataAccessService;
    private final DataSource dataSource;

    public DbAdminService(DataAccessService dataAccessService, DataSource dataSource) {
        this.dataAccessService = dataAccessService;
        this.dataSource = dataSource;
    }

    public Page<Map<String, Object>> executeQuery(String sql) {
        return executeQuery(sql, 0, 10);
    }

    @SuppressWarnings("squid:S1168")
    public Page<Map<String, Object>> executeQuery(String sql, int page, int pageSize) {
        sql = sql.trim();
        if (sql.toUpperCase().startsWith("SELECT")) {
            try {
                return dataAccessService.executeQuery(sql, page, pageSize);
            } catch (Exception e) {
                if (e.getCause() instanceof MappingException) {
                    return Page.empty();
                }
                throw e;
            }
        } else {
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
        return dataAccessService.getInfo();
    }

    public Page<Map<String, Object>> getData(String tableName, int page, int pageSize) {
        List<String> tables = getTables();
        Optional<String> first = tables.stream().filter(s -> s.equalsIgnoreCase(tableName)).findFirst();
        String tableNameFromDB = first.orElseThrow(() ->
                new SQLGrammarException("could not prepare statement", new SQLSyntaxErrorException("No such table " + tableName)));

        return dataAccessService.executeQuery(QUERY_SELECT_ALL + tableNameFromDB, page, pageSize);
    }

}
