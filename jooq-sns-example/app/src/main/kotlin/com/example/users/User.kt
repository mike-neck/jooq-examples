package com.example.users

data class User(
    val userId: UserId,
    val userKey: UserKey
)


data class UserId(val value: Long)

data class UserKey(val value: String)
