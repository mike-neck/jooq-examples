package com.example.app.temporaryusers

import com.example.Now
import com.example.Now.Companion.localDateTime
import com.example.app.emails.EmailAddress
import com.example.app.emails.EmailId
import com.example.nullable
import db.example.tables.Emails
import db.example.tables.TemporaryUsers
import org.jooq.DSLContext
import org.jooq.exception.NoDataFoundException
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
class TemporaryUserDaoImpl(
    private val dsl: DSLContext
): TemporaryUserDao {

    private val temporaryUsers: TemporaryUsers = TemporaryUsers.TEMPORARY_USERS

    private val emails: Emails = Emails.EMAILS

    private val String.temporaryHashKey: TemporaryHashKey
        get() =
            TemporaryHashKey(this)

    private val Int.emailId: EmailId get() = EmailId(this.toLong())

    private val LocalDateTime.asCreated: TemporaryUserCreated
        get() =
            TemporaryUserCreated(this.atOffset(ZoneOffset.UTC).toInstant())

    override fun save(temporaryUser: TemporaryUser): TemporaryUser? =
        dsl.insertInto(temporaryUsers)
            .columns(
                temporaryUsers.TEMPORARY_HASH_KEY,
                temporaryUsers.EMAIL_ID,
                temporaryUsers.CREATED)
            .values(
                temporaryUser.temporaryHashKey.value,
                temporaryUser.emailId.value.toInt(),
                temporaryUser.created.value.localDateTime
            ).returning()
            .fetchOptional()
            .map { TemporaryUser(it.temporaryHashKey.temporaryHashKey, it.emailId.emailId, it.created.asCreated) }
            .nullable

    override fun findValidTemporaryUserByEmail(emailAddress: EmailAddress, expiration: TemporaryUserExpiration, now: Now): TemporaryUser? =
        runCatching {
            dsl.select(temporaryUsers.TEMPORARY_HASH_KEY, temporaryUsers.EMAIL_ID, temporaryUsers.CREATED)
                .from(emails)
                .join(temporaryUsers).on(temporaryUsers.EMAIL_ID.eq(emails.EMAIL_ID))
                .where(
                    emails.EMAIL_VALUE.eq(emailAddress.value),
                    temporaryUsers.CREATED.greaterThan(now.localDateTime - expiration.duration)
                )
                .fetchSingle { TemporaryUser(it.value1().temporaryHashKey, it.value2().emailId, it.value3().asCreated) }
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                when (e) {
                    is NoDataFoundException -> null
                    else -> throw e
                }
            })
}
