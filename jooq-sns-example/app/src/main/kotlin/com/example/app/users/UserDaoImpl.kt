package com.example.app.users

import com.example.app.emails.EmailAddress
import db.example.tables.Emails
import db.example.tables.UserEmails
import db.example.tables.Users
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class UserDaoImpl(private val dsl: DSLContext): UserDao {

    val Int.userId: UserId get() = UserId(this.toLong())

    val String.userKey: UserKey get() = UserKey(this)

    override fun findByEmail(emailAddress: EmailAddress): User? =
        dsl.select(Users.USERS.USER_ID, Users.USERS.USER_KEY)
            .from(Users.USERS)
                .join(UserEmails.USER_EMAILS).onKey(UserEmails.USER_EMAILS.USER_ID)
                .join(Emails.EMAILS).onKey(UserEmails.USER_EMAILS.EMAIL_ID)
            .where(Emails.EMAILS.EMAIL_VALUE.eq(emailAddress.value))
            .fetchOne { User(it.value1().userId, it.value2().userKey) }
}
