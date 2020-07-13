package com.example.app.users

data class User(
    val userId: UserId,
    val userKey: UserKey
)


data class UserId(val value: Long): Comparable<UserId>
by object : Comparable<UserId> {
    override fun compareTo(other: UserId): Int = value.compareTo(other.value)
}

data class UserKey(val value: String)
