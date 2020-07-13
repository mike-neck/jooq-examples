package com.example.app

import com.example.Either
import com.example.Either.Companion.either
import com.example.Now
import com.example.app.emails.EmailAddress
import com.example.app.emails.EmailDao
import com.example.app.emails.EmailId
import com.example.app.temporaryusers.TemporaryHashKey
import com.example.app.temporaryusers.TemporaryHashKeyFactory
import com.example.app.temporaryusers.TemporaryUser
import com.example.app.temporaryusers.TemporaryUserCreated
import com.example.app.temporaryusers.TemporaryUserDao
import com.example.app.temporaryusers.TemporaryUserExpiration
import com.example.app.users.UserDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface CreateTemporaryUserUseCase {

    fun createNewTemporaryUser(emailAddress: EmailAddress): Either<String, TemporaryHashKey>
}

@Service
class CreateTemporaryUser(
    private val temporaryUserDao: TemporaryUserDao,
    private val userDao: UserDao,
    private val emailDao: EmailDao,
    private val idGen: IdGen,
    private val temporaryHashKeyFactory: TemporaryHashKeyFactory,
    private val expiration: TemporaryUserExpiration
) : CreateTemporaryUserUseCase {

    private fun any(vararg predicates: () -> Boolean): Boolean =
        predicates.any { it() }

    @Transactional
    override fun createNewTemporaryUser(emailAddress: EmailAddress): Either<String, TemporaryHashKey> =
        if (any(
                { temporaryUserDao.findValidTemporaryUserByEmail(emailAddress, expiration) != null },
                { userDao.findByEmail(emailAddress) != null }
            ))
            Either.left("user (${emailAddress.value}) already exists")
        else
            emailDao.save(idGen.id { EmailId(it) }, emailAddress)
                .either("failed to save email address")
                .flatMap { emailId ->
                    temporaryUserDao.save(
                        TemporaryUser(
                            temporaryHashKeyFactory.createNew(),
                            emailId,
                            TemporaryUserCreated(Now.get().instant)))
                        .either("failed to save temporary user")
                }
                .map { u: TemporaryUser -> u.temporaryHashKey }
}
