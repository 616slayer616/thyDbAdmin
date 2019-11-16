package org.padler.thydbadmin.service;

import org.hibernate.exception.SQLGrammarException;
import org.junit.jupiter.api.Test;
import org.padler.thydbadmin.AbstractSpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class DbAdminServiceTest extends AbstractSpringBootTest {

    @Autowired
    private DbAdminService dbAdminService;

    @Test
    void SELECT_ALL() {
        Page<Map<String, Object>> result = dbAdminService.executeQuery("SELECT * FROM USERS");
        assertTrue(result.getContent().isEmpty());
        assertTrue(result.getTotalElements() == 0);
    }

    @Test
    void INSERT_USER() {
        Page<Map<String, Object>> rolesBefore = dbAdminService.executeQuery("SELECT * FROM USER_ROLE");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (`USER_ROLE_ID`, `NAME`) VALUES (12, 'ROLE_USER')");
        Page<Map<String, Object>> rolesAfter = dbAdminService.executeQuery("SELECT * FROM USER_ROLE");

        assertThat(rolesAfter.getTotalElements(), is(rolesBefore.getTotalElements() + 1));
    }

    @Test
    void getTables() {
        List<String> tables = dbAdminService.getTables();
        assertThat(tables.size(), is(3));
    }

    @Test
    void getColumns() {
        List<String> users = dbAdminService.getColumns("USERS");
        List<String> usersRoles = dbAdminService.getColumns("USERS_ROLES");
        List<String> userRoles = dbAdminService.getColumns("USER_ROLE");
        assertThat(users.size(), is(6));
        assertThat(usersRoles.size(), is(2));
        assertThat(userRoles.size(), is(2));
    }

    @Test
    void getInfo() {
        DatabaseMetaData databaseMetaData = dbAdminService.getInfo();
        assertNotNull(databaseMetaData);
    }

    @Test
    void getData() {
        Page<Object[]> usersRoles = dbAdminService.getData("USER_ROLE", 0, 10);

        assertThat(usersRoles.getTotalElements(), is(11L));
    }

    @Test
    void getDataNoSuchTable() {
        assertThrows(SQLGrammarException.class, () -> dbAdminService.getData("TEST", 0, 10));
    }
}
