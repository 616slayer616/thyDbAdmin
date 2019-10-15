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
        dbAdminService.executeQuery("INSERT INTO USER_ROLE (`USER_ROLE_ID`, `NAME`) VALUES (1, 'ROLE_ADMIN')");
    }
}
