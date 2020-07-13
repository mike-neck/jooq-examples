package com.example.temporaryusers

import com.example.Now
import com.example.TableFactory
import com.example.UsingDatabase
import com.example.emails.EmailAddress
import com.example.emails.EmailDao
import com.example.emails.EmailDaoTest
import com.example.emails.EmailId
import db.fixture.TestSetup
import org.assertj.core.api.Assertions.assertThat
import org.assertj.db.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.transaction.support.TransactionTemplate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.test.Test

@UsingDatabase
@Import(
    TemporaryUserDaoTest.Config::class,
    EmailDaoTest.Config::class)
class TemporaryUserDaoTest {

    @Autowired
    lateinit var testSetup: TestSetup

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    lateinit var emailDao: EmailDao

    @Autowired
    lateinit var temporaryUserDao: TemporaryUserDao

    @Autowired
    lateinit var tableFactory: TableFactory

    @BeforeEach
    fun setup() {
        testSetup.clean()
    }

    @Test
    fun save() {
        val instant = OffsetDateTime.of(2020, 1, 2, 15, 4, 5, 6, ZoneOffset.UTC).toInstant()
        val now = Now(instant)
        val user = transactionTemplate.execute {
            val emailId = EmailId(200L)
            emailDao.save(emailId, EmailAddress("test", "example.com"), now)
            val temporaryUser = TemporaryUser(TemporaryHashKey("22bb33cc44dd"), emailId, TemporaryUserCreated(instant))
            temporaryUserDao.save(temporaryUser)
        }

        assertThat(user)
            .isNotNull
            .satisfies { assertThat(it?.emailId).isEqualTo(EmailId(200L)) }
            .satisfies { assertThat(it?.created).isEqualTo(TemporaryUserCreated(instant.truncatedTo(ChronoUnit.MILLIS))) }

        val temporaryUsers = tableFactory.table("temporary_users")

        assertThat(temporaryUsers)
            .column("temporary_hash_key")
            .value().isEqualTo("22bb33cc44dd")
            .column("email_id")
            .value().isEqualTo(200L)
    }

    @Configuration
    class Config {

        @Suppress("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        fun temporaryUserRepository(dsl: DSLContext): TemporaryUserDao =
            TemporaryUserDaoImpl(dsl)
    }
}
