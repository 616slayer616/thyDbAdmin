package org.padler.thydbadmin;

import org.padler.thydbadmin.service.DbAdminService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    private final DbAdminService dbAdminService;

    public DatabaseInitializer(DbAdminService dbAdminService) {
        this.dbAdminService = dbAdminService;
    }

    @Bean
    public CommandLineRunner initExampleData(DatabaseInitializer initializer) {
        return args -> initializer.initExampleData();
    }

    public void initExampleData() {
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (USER_ROLE_ID, NAME) VALUES (1, 'ROLE_1')");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (USER_ROLE_ID, NAME) VALUES (2, 'ROLE_2')");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (USER_ROLE_ID, NAME) VALUES (3, 'ROLE_3')");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (USER_ROLE_ID, NAME) VALUES (4, 'ROLE_4')");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (USER_ROLE_ID, NAME) VALUES (5, 'ROLE_5')");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (USER_ROLE_ID, NAME) VALUES (6, 'ROLE_6')");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (USER_ROLE_ID, NAME) VALUES (7, 'ROLE_7')");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (USER_ROLE_ID, NAME) VALUES (8, 'ROLE_8')");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (USER_ROLE_ID, NAME) VALUES (9, 'ROLE_9')");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (USER_ROLE_ID, NAME) VALUES (10, 'ROLE_10')");
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (USER_ROLE_ID, NAME) VALUES (11, 'ROLE_11')");
        dbAdminService.executeQuery("INSERT INTO UUID_TABLE (UUID) VALUES ('123e4567-e89b-12d3-a456-426614174000')");
    }
}
