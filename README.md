# thyDbAdmin
[![Build Status](https://github.com/616slayer616/thyDbAdmin/actions/workflows/gradle.yml/badge.svg)](https://github.com/616slayer616/thyDbAdmin/actions/workflows/gradle.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=616slayer616_thyDbAdmin&metric=alert_status)](https://sonarcloud.io/dashboard?id=616slayer616_thyDbAdmin)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=616slayer616_thyDbAdmin&metric=bugs)](https://sonarcloud.io/dashboard?id=616slayer616_thyDbAdmin)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=616slayer616_thyDbAdmin&metric=code_smells)](https://sonarcloud.io/dashboard?id=616slayer616_thyDbAdmin)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=616slayer616_thyDbAdmin&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=616slayer616_thyDbAdmin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=616slayer616_thyDbAdmin&metric=coverage)](https://sonarcloud.io/dashboard?id=616slayer616_thyDbAdmin)

Inspired by phpMyAdmin and phpPgAdmin, ThyDbAdmin aims to integrate a database management tool into your Spring Boot application.

## Usage

### Gradle
```
implementation 'org.padler:thyDbAdmin:1.2.0'
```

### Maven
```
<dependency>
  <groupId>org.padler</groupId>
  <artifactId>thyDbAdmin</artifactId>
  <version>1.2.0</version>
</dependency>
```

### Configuration

Enable thyDbAdmin Controllers (without this you can access the services but not the /thyDbAdmin URLs)
```
thyDbAdmin:
  controller: true
```

Disable Flyway auto configuration, in case of a FlywayException on startup
```
thyDbAdmin:
  saveMode.enabled: true
```

#### Security configuration (optional)

Let only "ADMIN" users access thyDbAdmin
```
@Override
protected void configure(HttpSecurity http) throws Exception {
     http
        .authorizeRequests()
        .antMatchers("/thyDbAdmin/**").hasRole("ADMIN");
}
```

### Start

Go to http://localhost:8080/thyDbAdmin

Overview of the tables:
![Overview](/docs/img/overview.png)

---

Query result:
![Select](/docs/img/select.png)

---

Database information:
![Database info](/docs/img/db_info.png)

---
