package com.example.app.temporaryusers

interface TemporaryUserDao {

    fun save(temporaryUser: TemporaryUser): TemporaryUser?
}