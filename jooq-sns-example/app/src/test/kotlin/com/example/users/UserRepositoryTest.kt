package com.example.users

import com.example.UsingDatabase
import com.example.emails.EmailAddress
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.Test

@UsingDatabase
@Import(UserRepositoryTest.Config::class)
class UserRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    @Test
    fun findExistingUser() {
        transactionTemplate.execute {
            val user = userRepository.findByEmail(EmailAddress("user-1", "example.com"))
            assertThat(user).isNotNull
        }
    }

    @Configuration
    class Config {

        @Suppress("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        fun userRepository(dsl: DSLContext): UserRepository = UserRepositoryImpl(dsl)
    }
}
