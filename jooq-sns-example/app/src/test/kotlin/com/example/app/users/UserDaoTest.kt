package com.example.app.users

import com.example.UsingDatabase
import com.example.app.emails.EmailAddress
import com.example.app.emails.EmailId
import db.example.tables.records.EmailsRecord
import db.example.tables.records.PasswordsRecord
import db.example.tables.records.TemporaryUsersRecord
import db.example.tables.records.UserEmailsRecord
import db.example.tables.records.UsersRecord
import db.fixture.TestSetup
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

@UsingDatabase
@Import(UserDaoTest.Config::class)
class UserDaoTest {

    @Autowired
    lateinit var testSetup: TestSetup

    @Autowired
    lateinit var userDao: UserDao

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    private fun LongArray.next(): Long = this[0].also { this[0] = this[0] + 1 }

    private fun LongArray.withNext(action: (Long) -> Unit): Long =
        this.next().apply(action)

    private val Long.emailId: EmailId get() = EmailId(this)
    private val Long.userId: UserId get() = UserId(this)

    @BeforeEach
    fun setup() {
        testSetup.clean()
        val id = LongArray(1).also { it[0] = 1L }

        val createNewEmailRecord: (Long) -> Unit = { testSetup.insert(EmailsRecord(it.toInt(), "user-$it@example.com", LocalDateTime.of(2020, 1, it.toInt(), 15, 4, 5))) }
        val emailId1 = id.withNext(createNewEmailRecord)
        val userId1 = id.withNext {
            testSetup.insert(UsersRecord(it.toInt(), "test-user", "石田三成"))
            testSetup.insert(PasswordsRecord(it.toInt(), "aa11bb2233cc"))
            testSetup.insert(UserEmailsRecord(emailId1.toInt(), it.toInt(), LocalDateTime.of(2020, 1, emailId1.toInt(), 15, 4, 5)))
        }

        val emailId2 = id.withNext(createNewEmailRecord)
        id.withNext {
            testSetup.insert(TemporaryUsersRecord("aa00bb11cc22", emailId2.toInt(), LocalDateTime.of(2020, 1, emailId1.toInt(), 15, 4, 5)))
        }

        val emailId3 = id.withNext(createNewEmailRecord)
        val userId3 = id.withNext {
            testSetup.insert(UsersRecord(it.toInt(), "ankoku", "安国寺恵瓊"))
            testSetup.insert(PasswordsRecord(it.toInt(), "aa11bb2233cc"))
            testSetup.insert(UserEmailsRecord(emailId3.toInt(), it.toInt(), LocalDateTime.of(2020, 1, emailId1.toInt(), 15, 4, 5)))
        }

        val emailId4 = id.withNext(createNewEmailRecord)
        val emailId5 = id.withNext(createNewEmailRecord)
        val userId4 = id.withNext {
            testSetup.insert(UsersRecord(it.toInt(), "kikkawa", "吉川元春"))
            testSetup.insert(PasswordsRecord(it.toInt(), "aa22bb11cc33"))
            testSetup.insert(UserEmailsRecord(emailId4.toInt(), it.toInt(), LocalDateTime.of(2020, 1, emailId1.toInt(), 15, 4, 5)))
            testSetup.insert(UserEmailsRecord(emailId5.toInt(), it.toInt(), LocalDateTime.of(2020, 1, emailId1.toInt(), 15, 4, 5)))
        }

        testIds = sortedMapOf(
            userId1.userId to setOf(emailId1.emailId),
            userId3.userId to setOf(emailId3.emailId),
            userId4.userId to setOf(emailId4.emailId, emailId5.emailId)
        )
        notRegisteredEmailId = emailId2.emailId
    }

    var testIds: SortedMap<UserId, Set<EmailId>> = sortedMapOf()
    lateinit var notRegisteredEmailId: EmailId

    @Test
    fun findExistingUser() {
        transactionTemplate.execute {
            testIds.values.flatMap { it.toList() }.forEach {
                val user = userDao.findByEmail(EmailAddress("user-${it.value}", "example.com"))
                assertThat(user).isNotNull
            }
        }
    }

    @Configuration
    class Config {

        @Suppress("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        fun userRepository(dsl: DSLContext): UserDao = UserDaoImpl(dsl)
    }
}
