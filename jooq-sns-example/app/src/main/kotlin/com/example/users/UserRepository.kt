package com.example.users

import com.example.emails.EmailAddress


interface UserRepository {

    fun findByEmail(emailAddress: EmailAddress): User?
}
