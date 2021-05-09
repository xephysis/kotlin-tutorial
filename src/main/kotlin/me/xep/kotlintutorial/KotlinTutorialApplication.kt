package me.xep.kotlintutorial

import me.xep.kotlintutorial.config.DatastoreConfig
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication(exclude = [R2dbcDataAutoConfiguration::class, R2dbcAutoConfiguration::class])
class KotlinTutorialApplication

fun main(args: Array<String>) {
	runApplication<KotlinTutorialApplication>(*args)
}
