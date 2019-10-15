package org.padler.thydbadmin.service;

import org.hibernate.exception.SQLGrammarException;
import org.junit.jupiter.api.Test;
import org.padler.thydbadmin.TestApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestApplication.class)
class DbAdminServiceTest {

    @Autowired
    private DbAdminService dbAdminService;

    @Test
    void SELECT_ALL() {
        List list = dbAdminService.executeQuery("SELECT * FROM USERS");
        assertTrue(list.isEmpty());
    }

    @Test
    void INSERT_USER() {
        List rolesBefore = dbAdminService.executeQuery("SELECT * FROM USER_ROLE");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (`USER_ROLE_ID`, `NAME`) VALUES (2, 'ROLE_USER')");
        List rolesAfter = dbAdminService.executeQuery("SELECT * FROM USER_ROLE");

        assertThat(rolesAfter.size(), is(rolesBefore.size() + 1));
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
    void getData() {
        Page<Object[]> usersRoles = dbAdminService.getData("USER_ROLE", 0, 10);

        assertThat(usersRoles.getTotalElements(), is(1L));
    }

    @Test
    void getDataNoSuchTable() {
        assertThrows(SQLGrammarException.class, () -> dbAdminService.getData("TEST", 0, 10));
    }
}
