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
import static org.junit.jupiter.api.Assertions.assertThrows;

class DbAdminServiceTest extends AbstractSpringBootTest {

    @Autowired
    private DbAdminService dbAdminService;

    @Test
    void SELECT_ALL() {
        Page<Map<String, Object>> result = dbAdminService.executeQuery("SELECT * FROM users");
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void SELECT_ALL_UUID() {
        Page<Map<String, Object>> result = dbAdminService.executeQuery("SELECT * FROM uuid_table");
        assertThat(result.getTotalElements()).isOne();
    }

    @Test
    void INSERT_USER() {
        Page<Map<String, Object>> rolesBefore = dbAdminService.executeQuery("SELECT * FROM user_role");
        dbAdminService.executeQuery("INSERT INTO user_role (USER_ROLE_ID, NAME) VALUES (12, 'ROLE_USER')");
        Page<Map<String, Object>> rolesAfter = dbAdminService.executeQuery("SELECT * FROM user_role");

        assertThat(rolesAfter.getTotalElements()).isEqualTo(rolesBefore.getTotalElements() + 1);
    }

    @Test
    void getTables() {
        List<String> tables = dbAdminService.getTables();
        assertThat(tables.size()).isEqualTo(4);
    }

    @Test
    void getColumns() {
        List<String> users = dbAdminService.getColumns("users");
        List<String> usersRoles = dbAdminService.getColumns("users_roles");
        List<String> userRoles = dbAdminService.getColumns("user_role");
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
    void getDataWIthUUID() {
        Page<Map<String, Object>> uuids = dbAdminService.getData("uuid_table", 0, 10);

        assertThat(uuids.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void getData() {
        Page<Map<String, Object>> usersRoles = dbAdminService.getData("user_role", 0, 10);

        assertThat(usersRoles.getTotalElements()).isEqualTo(11L);
    }

    @Test
    void getDataNoSuchTable() {
        assertThrows(SQLGrammarException.class, () -> dbAdminService.getData("TEST", 0, 10));
    }
}
