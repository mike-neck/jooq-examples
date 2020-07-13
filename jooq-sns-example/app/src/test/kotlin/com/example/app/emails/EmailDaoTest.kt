package com.example.app.emails

import com.example.Now
import com.example.TableFactory
import com.example.UsingDatabase
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
import kotlin.test.Test

@UsingDatabase
@Import(EmailDaoTest.Config::class)
class EmailDaoTest {

    @Autowired
    lateinit var testSetup: TestSetup

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    lateinit var emailDao: EmailDao

    @Autowired
    lateinit var tableFactory: TableFactory

    @BeforeEach
    fun setup() {
        testSetup.clean()
    }

    @Test
    fun save() {
        val instant = OffsetDateTime.of(2020, 1, 2, 15, 4, 5, 0, ZoneOffset.UTC).toInstant()
        val now = Now(instant)
        val emailId = transactionTemplate.execute {
            emailDao.save(EmailId(200), EmailAddress("user-1", "example.com"), now)
        }
        assertThat(emailId).isNotNull
        val id = emailId as EmailId
        val emails = tableFactory.table("emails")
        assertThat(emails)
            .column("email_id")
            .value().isEqualTo(id.value)
    }

    @Configuration
    class Config {

        @Suppress("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        fun emailRepository(dsl: DSLContext): EmailDao = EmailDaoImpl(dsl)
    }
}
