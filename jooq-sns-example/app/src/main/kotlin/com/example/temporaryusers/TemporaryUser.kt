package com.example.temporaryusers

import com.example.Now
import com.example.emails.EmailId
import java.time.Duration
import java.time.Instant
import kotlin.random.Random

data class TemporaryUser(
    val temporaryHashKey: TemporaryHashKey,
    val emailId: EmailId,
    val created: TemporaryUserCreated
) {

    fun isExpired(withIn: TemporaryUserExpiration, now: Now = Now.get()): Boolean =
        created.value + withIn.duration <= now.instant
}

data class TemporaryHashKey(val value: String)

interface TemporaryHashKeyFactory {
    fun createNew(): TemporaryHashKey
}

class DefaultTemporaryHashKeyFactory(private val random : Random): TemporaryHashKeyFactory {

    override fun createNew(): TemporaryHashKey =
        TemporaryHashKey(
            (1..4).map { random.nextInt() }
                .joinToString("-") { it.toString(16) })

}

data class TemporaryUserCreated(val value: Instant)

data class TemporaryUserExpiration(val duration: Duration)
