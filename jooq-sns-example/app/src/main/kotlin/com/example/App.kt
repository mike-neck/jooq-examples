package com.example

import com.example.emails.EmailAddress
import com.example.users.UserDao
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.util.*
import kotlin.reflect.KClass

@SpringBootApplication
class App {

    val logger = logger<App>()

    @Bean
    fun commandLineRunner(userDao: UserDao): CommandLineRunner =
        CommandLineRunner {
            val user = userDao.findByEmail(EmailAddress("user-1", "example.com"))
            logger.info("user of user-1, example.com = {}", user)
        }
}

fun main(args:Array<String>) {
    SpringApplication.run(App::class.java, *args)
}

class Logger<T: Any>(logger: org.slf4j.Logger): org.slf4j.Logger by logger

inline fun <reified T: Any> logger(klass: KClass<T> = T::class): Logger<T> = Logger(LoggerFactory.getLogger(klass.java))

val <T: Any> Optional<T>.nullable: T? get() =
    if (this.isPresent) this.get()
    else null
