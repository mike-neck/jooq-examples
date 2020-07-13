package com.example

import com.example.app.IdGen

object TestIdGen: IdGen {
    override fun long(): Long = System.currentTimeMillis() * 100

    override fun longs(size: Int): LongArray =
        (1..size).map { long() + it - 1 }.toLongArray()
}
