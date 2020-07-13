package com.example.emails

import com.example.Now
import com.example.nullable
import db.example.tables.Emails
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class EmailDaoImpl(
    private val dsl: DSLContext
): EmailDao {

    val emails: Emails = Emails.EMAILS

    private val Long.emailId: EmailId get() = EmailId(this)

    override fun save(emailId: EmailId, emailAddress: EmailAddress, now: Now): EmailId? =
        dsl.insertInto(emails)
            .columns(emails.EMAIL_ID, emails.EMAIL_VALUE, emails.CREATED)
            .values(emailId.value.toInt(), emailAddress.value, now.localDateTime)
            .returning()
            .fetchOptional()
            .map { it.emailId.toLong().emailId }
            .nullable
}
