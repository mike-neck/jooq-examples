package com.example.app.emails

import com.example.Now

interface EmailDao {

    fun save(emailId: EmailId, emailAddress: EmailAddress, now: Now = Now.get()): EmailId?
}
