package com.example.app.temporaryusers

import com.example.Now
import com.example.app.emails.EmailAddress

interface TemporaryUserDao {

    fun save(temporaryUser: TemporaryUser): TemporaryUser?

    fun findValidTemporaryUserByEmail(emailAddress: EmailAddress, expiration: TemporaryUserExpiration, now: Now = Now.get()): TemporaryUser?
}
