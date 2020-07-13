package com.example.users

import com.example.emails.EmailAddress


interface UserDao {

    fun findByEmail(emailAddress: EmailAddress): User?
}
