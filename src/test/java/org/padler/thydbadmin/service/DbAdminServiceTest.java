package org.padler.thydbadmin.service;

import org.hibernate.exception.SQLGrammarException;
import org.junit.jupiter.api.Test;
import org.padler.thydbadmin.AbstractSpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DbAdminServiceTest extends AbstractSpringBootTest {

    @Autowired
    private DbAdminService dbAdminService;

    @Test
    void SELECT_ALL() {
        Page<Map<String, Object>> result = dbAdminService.executeQuery("SELECT * FROM USERS");
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void INSERT_USER() {
        Page<Map<String, Object>> rolesBefore = dbAdminService.executeQuery("SELECT * FROM USER_ROLE");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (`USER_ROLE_ID`, `NAME`) VALUES (12, 'ROLE_USER')");
        Page<Map<String, Object>> rolesAfter = dbAdminService.executeQuery("SELECT * FROM USER_ROLE");

        assertThat(rolesAfter.getTotalElements()).isEqualTo(rolesBefore.getTotalElements() + 1);
    }

    @Test
    void getTables() {
        List<String> tables = dbAdminService.getTables();
        assertThat(tables.size()).isEqualTo(3);
    }

    @Test
    void getColumns() {
        List<String> users = dbAdminService.getColumns("USERS");
        List<String> usersRoles = dbAdminService.getColumns("USERS_ROLES");
        List<String> userRoles = dbAdminService.getColumns("USER_ROLE");
        assertThat(users.size()).isEqualTo(6);
        assertThat(usersRoles.size()).isEqualTo(2);
        assertThat(userRoles.size()).isEqualTo(2);
    }

    @Test
    void getInfo() {
        DatabaseMetaData databaseMetaData = dbAdminService.getInfo();
        assertThat(databaseMetaData).isNotNull();
    }

    @Test
    void getData() {
        Page<Object[]> usersRoles = dbAdminService.getData("USER_ROLE", 0, 10);

        assertThat(usersRoles.getTotalElements()).isEqualTo(11L);
    }

    @Test
    void getDataNoSuchTable() {
        assertThrows(SQLGrammarException.class, () -> dbAdminService.getData("TEST", 0, 10));
    }
}
