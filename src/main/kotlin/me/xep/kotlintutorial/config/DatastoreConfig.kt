package me.xep.kotlintutorial.config

import io.r2dbc.h2.H2ConnectionConfiguration
import io.r2dbc.h2.H2ConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.r2dbc.core.DatabaseClient

@Profile("h2")
@Configuration
@EnableR2dbcRepositories
class DatastoreConfig : AbstractR2dbcConfiguration() {

//    @Value("\${spring.datasource.username}")
//    private val userName: String = ""
//
//    @Value("\${spring.datasource.password}")
//    private val password: String = ""
//
//    @Value("\${spring.datasource.dbname}")
//    private val dbName: String = ""

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        return H2ConnectionFactory(H2ConnectionConfiguration.builder()
                .url("mem:testdb;DB_CLOSE_DELAY=-1;")
                .username("sa")
                .build())
    }

    @Bean
    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
        val populate = ResourceDatabasePopulator(ClassPathResource("schema.sql"))
        initializer.setDatabasePopulator(populate)

        return initializer
    }
}