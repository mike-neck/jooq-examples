package com.example.users

data class User(
    val userId: UserId,
    val userKey: UserKey
)


data class UserId(val value: Long)

data class UserKey(val value: String)

data class EmailAddress(val local: String, val domain: String) {
    val value: String get() = "$local@$domain"
}
