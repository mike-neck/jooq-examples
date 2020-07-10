package com.example.temporaryusers

interface TemporaryUserRepository {

    fun save(temporaryUser: TemporaryUser): TemporaryUser?
}