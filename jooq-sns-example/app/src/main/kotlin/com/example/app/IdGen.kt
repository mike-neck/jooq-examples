package com.example.app

interface IdGen {

    fun long(): Long

    fun longs(size: Int): LongArray

    fun <T: Any> id(cons: (Long) -> T): T = cons(long())

    fun <T: Any> ids(size: Int, cons: (Long) -> T): Iterable<T> =
        (1..size).map { cons(long()) }
}
