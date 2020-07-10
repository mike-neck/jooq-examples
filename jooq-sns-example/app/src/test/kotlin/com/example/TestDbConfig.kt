package com.example

import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration
import org.springframework.boot.autoconfigure.jooq.JooqProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.TransactionTemplate
import java.lang.annotation.Inherited
import javax.sql.DataSource

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Inherited
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestDbConfig::class])
@TestPropertySource(locations = ["classpath:application.properties"])
@Import(JooqAutoConfiguration::class)
annotation class UsingDatabase

@Configuration
@EnableConfigurationProperties(DataSourceProperties::class, JooqProperties::class)
@EnableTransactionManagement
class TestDbConfig {

    @Bean
    fun dataSource(dataSourceProperties: DataSourceProperties): DataSource =
        dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()

    @Bean
    fun platformTransactionManager(dataSource: DataSource): PlatformTransactionManager =
        DataSourceTransactionManager(dataSource)

    @Bean
    fun transactionTemplate(platformTransactionManager: PlatformTransactionManager): TransactionTemplate =
        TransactionTemplate(platformTransactionManager)
}
