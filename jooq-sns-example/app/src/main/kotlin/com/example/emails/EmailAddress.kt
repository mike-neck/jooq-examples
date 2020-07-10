package com.example.emails

data class EmailAddress(val local: String, val domain: String) {
    val value: String get() = "$local@$domain"
}
