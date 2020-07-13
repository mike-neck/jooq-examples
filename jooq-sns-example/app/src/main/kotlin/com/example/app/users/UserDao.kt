package com.example.app.users

import com.example.app.emails.EmailAddress


interface UserDao {

    fun findByEmail(emailAddress: EmailAddress): User?
}
