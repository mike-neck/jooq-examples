package com.example

import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

data class Now(val instant: Instant) {

    val localDateTime: LocalDateTime get() = instant.localDateTime

    companion object {

        fun get(clock: Clock = Clock.systemUTC()): Now = Now(Instant.now(clock))

        val Instant.localDateTime: LocalDateTime get() =
            this.atOffset(ZoneOffset.UTC).toLocalDateTime()
    }
}
