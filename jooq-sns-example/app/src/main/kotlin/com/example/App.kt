package com.example

import com.example.users.EmailAddress
import com.example.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import kotlin.reflect.KClass

@SpringBootApplication
class App {

    val logger = logger<App>()

    @Bean
    fun commandLineRunner(userRepository: UserRepository): CommandLineRunner =
        CommandLineRunner {
            val user = userRepository.findByEmail(EmailAddress("user-1", "example.com"))
            logger.info("user of user-1, example.com = {}", user)
        }
}

fun main(args:Array<String>) {
    SpringApplication.run(App::class.java, *args)
}

class Logger<T: Any>(logger: org.slf4j.Logger): org.slf4j.Logger by logger

inline fun <reified T: Any> logger(klass: KClass<T> = T::class): Logger<T> = Logger(LoggerFactory.getLogger(klass.java))
