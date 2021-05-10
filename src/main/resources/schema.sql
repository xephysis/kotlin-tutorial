-- https://www.baeldung.com/spring-boot-data-sql-and-schema-sql
CREATE TABLE IF NOT EXISTS PRODUCT (
    `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(128) NOT NULL,
    `price` DOUBLE NOT NULL
);