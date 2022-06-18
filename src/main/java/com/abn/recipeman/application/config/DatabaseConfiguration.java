package com.abn.recipeman.application.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableAutoConfiguration
@ComponentScan({"com.abn.recipeman"})
@EnableJpaRepositories({"com.abn.recipeman.persistence.repository"})
@EntityScan({"com.abn.recipeman.domain.model.entity"})
@EnableTransactionManagement
public class DatabaseConfiguration {

}
