package com.example.app.temporaryusers

import com.example.Now
import com.example.TableFactory
import com.example.UsingDatabase
import com.example.app.emails.EmailAddress
import com.example.app.emails.EmailDao
import com.example.app.emails.EmailDaoTest
import com.example.app.emails.EmailId
import db.example.tables.records.EmailsRecord
import db.example.tables.records.TemporaryUsersRecord
import db.example.tables.records.UserEmailsRecord
import db.example.tables.records.UsersRecord
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
import java.time.Duration
import java.time.LocalDateTime
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

    @Test
    fun temporaryUserExisting() {
        val localDateTime = LocalDateTime.of(2020, 1, 2, 15, 0, 0, 0)
        val expiration = TemporaryUserExpiration(Duration.of(7L, ChronoUnit.DAYS))

        testSetup.insert(EmailsRecord(1, "user@example.com", localDateTime))
        testSetup.insert(EmailsRecord(2, "another@example.com", localDateTime))

        testSetup.insert(TemporaryUsersRecord("aa11bb22cc", 1, localDateTime))
        testSetup.insert(UsersRecord(3, "another", "石田三成"))
        testSetup.insert(UserEmailsRecord(2, 3, localDateTime))

        val instant = OffsetDateTime.of(2020, 1, 3, 15, 0, 0, 0, ZoneOffset.UTC).toInstant()
        val now = Now(instant)

        val temporaryUser = transactionTemplate.execute {
            temporaryUserDao.findValidTemporaryUserByEmail(EmailAddress("user", "example.com"), expiration, now)
        }

        assertThat(temporaryUser).isNotNull
    }

    @Test
    fun temporaryUserNotExisting() {
        val expiration = TemporaryUserExpiration(Duration.of(7L, ChronoUnit.DAYS))
        val instant = OffsetDateTime.of(2020, 1, 3, 15, 0, 0, 0, ZoneOffset.UTC).toInstant()
        val now = Now(instant)

        val temporaryUser = transactionTemplate.execute {
            temporaryUserDao.findValidTemporaryUserByEmail(EmailAddress("user", "example.com"), expiration, now)
        }

        assertThat(temporaryUser).isNull()
    }

    @Configuration
    class Config {

        @Suppress("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        fun temporaryUserRepository(dsl: DSLContext): TemporaryUserDao =
            TemporaryUserDaoImpl(dsl)
    }
}
