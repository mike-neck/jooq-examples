package com.example.emails

import com.example.Now

interface EmailRepository {

    fun save(emailId: EmailId, emailAddress: EmailAddress, now: Now = Now.get()): EmailId?
}
