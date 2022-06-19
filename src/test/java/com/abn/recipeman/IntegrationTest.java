package com.abn.recipeman;

import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = RecipeManApplication.class,
        properties = {
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "spring.datasource.type=com.zaxxer.hikari.HikariDataSource",
                "spring.datasource.driverClassName=",
                "spring.datasource.url=jdbc:h2:mem:recipeman;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        })

public @interface IntegrationTest {
}
