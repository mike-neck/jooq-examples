package com.example.app

import com.example.TableFactory
import com.example.TestIdGen
import com.example.UsingDatabase
import com.example.app.emails.EmailAddress
import com.example.app.emails.EmailDao
import com.example.app.emails.EmailDaoTest
import com.example.app.temporaryusers.TemporaryHashKey
import com.example.app.temporaryusers.TemporaryHashKeyFactory
import com.example.app.temporaryusers.TemporaryUserDao
import com.example.app.temporaryusers.TemporaryUserDaoTest
import com.example.app.temporaryusers.TemporaryUserExpiration
import com.example.app.users.UserDao
import com.example.app.users.UserDaoImpl
import db.example.tables.records.EmailsRecord
import db.example.tables.records.UserEmailsRecord
import db.example.tables.records.UsersRecord
import db.fixture.TestSetup
import org.assertj.core.api.Assertions.assertThat
import org.assertj.db.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.transaction.support.TransactionTemplate
import java.time.Duration
import java.time.LocalDateTime

@UsingDatabase
@Import(
    TemporaryUserDaoTest.Config::class,
    EmailDaoTest.Config::class,
    CreateTemporaryUserUseCaseTest.Config::class
)
class CreateTemporaryUserUseCaseTest {

    @Autowired
    lateinit var testSetup: TestSetup

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    lateinit var tableFactory: TableFactory

    @Autowired
    lateinit var createTemporaryUserUseCase: CreateTemporaryUserUseCase

    @Autowired
    lateinit var temporaryHashKeyFactory: TemporaryHashKeyFactory

    @BeforeEach
    fun setup() {
        testSetup.clean()
    }

    @Test
    fun noTemporaryUserAndNoExistingUser() {
        val emailId = TestIdGen.long()
        val userId = TestIdGen.long()
        val localDateTime = LocalDateTime.of(2020, 1, 2, 15, 0, 0, 0)

        testSetup.insert(EmailsRecord(emailId.toInt(), "user@example.com", localDateTime))
        testSetup.insert(UsersRecord(userId.toInt(), "user", "石田三成"))
        testSetup.insert(UserEmailsRecord(emailId.toInt(), userId.toInt(), localDateTime))

        val either = createTemporaryUserUseCase.createNewTemporaryUser(EmailAddress("admin", "example.com"))

        val temporaryUsers = tableFactory.table("temporary_users")

        transactionTemplate.executeWithoutResult {
            assertAll(
                { assertThat(either.isRight).isTrue() },
                {
                    assertThat(temporaryUsers)
                        .hasNumberOfRows(1)
                        .column("temporary_hash_key").value().isEqualTo(temporaryHashKeyFactory.createNew().value)
                }
            )
        }
    }

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Configuration
    class Config {
        @Bean
        fun userDao(dsl: DSLContext): UserDao = UserDaoImpl(dsl)

        @Bean
        fun temporaryHashKeyFactory(): TemporaryHashKeyFactory =
            object : TemporaryHashKeyFactory {
                override fun createNew(): TemporaryHashKey = TemporaryHashKey("1100aa22")
            }

        @Bean
        fun useCase(
            temporaryHashKeyFactory: TemporaryHashKeyFactory,
            temporaryUserDao: TemporaryUserDao,
            userDao: UserDao,
            emailDao: EmailDao
        ): CreateTemporaryUserUseCase =
            CreateTemporaryUser(
                temporaryUserDao,
                userDao,
                emailDao,
                TestIdGen,
                temporaryHashKeyFactory,
                TemporaryUserExpiration(Duration.ofDays(1)))
    }
}
