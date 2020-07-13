package com.example.temporaryusers

interface TemporaryUserDao {

    fun save(temporaryUser: TemporaryUser): TemporaryUser?
}