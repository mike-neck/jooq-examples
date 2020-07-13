package com.example.app.temporaryusers

import com.example.Now.Companion.localDateTime
import com.example.app.emails.EmailId
import com.example.nullable
import db.example.tables.TemporaryUsers
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
class TemporaryUserDaoImpl(
    private val dsl: DSLContext
): TemporaryUserDao {

    private val temporaryUsers: TemporaryUsers = TemporaryUsers.TEMPORARY_USERS

    private val String.temporaryHashKey: TemporaryHashKey get() =
        TemporaryHashKey(this)

    private val Int.emailId: EmailId get() = EmailId(this.toLong())

    private val LocalDateTime.asCreated: TemporaryUserCreated get() =
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
}