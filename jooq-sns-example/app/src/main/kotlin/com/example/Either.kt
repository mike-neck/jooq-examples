package com.example

interface Either<L: Any, R: Any> {
    val isLeft: Boolean get() = !isRight
    val isRight: Boolean

    fun <N: Any> map(mapping: (R) -> N): Either<L, N>

    fun <N: Any> flatMap(mapping: (R) -> Either<L, N>): Either<L, N>

    fun <E: Any> errorMap(mapping: (L) -> E): Either<E, R>

    fun fromLeft(orElse: (R) -> L): L

    fun fromRight(orElse: (L) -> R): R

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        fun <L: Any, R: Any> right(value: R): Either<L, R> = Right(value)
        fun <L: Any, R: Any> left(value: L): Either<L, R> = Left(value)

        inline fun <R: Any> fromTryCatch(action: () -> R): Either<Throwable, R> =
            runCatching(action)
                .fold(onSuccess = { right(it) }, onFailure = {
                    when (it) {
                        is OutOfMemoryError -> throw it
                        else -> left(it)
                    }
                })

        operator fun <R: Any, N: Any> Either<ErrorContext, R>.invoke(actionContext: String, action: (R) -> Either<Throwable, N>): Either<ErrorContext, N> =
            this.flatMap { action(it).errorMap { e -> ErrorContext(actionContext, e) } }

        fun <L: Any, R: Any> R?.either(left: L): Either<L, R> =
            this?.let { right<L, R>(it) } ?: left(left)
    }
}

private data class Right<L: Any, R: Any>(private val value: R): Either<L, R> {

    override val isRight: Boolean get() = true

    override fun <N : Any> map(mapping: (R) -> N): Either<L, N> = Right(mapping(value))

    override fun <N : Any> flatMap(mapping: (R) -> Either<L, N>): Either<L, N> = mapping(value)

    override fun <E : Any> errorMap(mapping: (L) -> E): Either<E, R> = Right(value)

    override fun fromLeft(orElse: (R) -> L): L = orElse(value)

    override fun fromRight(orElse: (L) -> R): R = value
}

private data class Left<L: Any, R: Any>(private val value: L): Either<L, R> {

    override val isRight: Boolean get() = false

    override fun <N : Any> map(mapping: (R) -> N): Either<L, N> = Left(value)

    override fun <N : Any> flatMap(mapping: (R) -> Either<L, N>): Either<L, N> = Left(value)

    override fun <E : Any> errorMap(mapping: (L) -> E): Either<E, R> = Left(mapping(value))

    override fun fromLeft(orElse: (R) -> L): L = value

    override fun fromRight(orElse: (L) -> R): R = orElse(value)
}

data class ErrorContext(val actionContext: String, val error: Throwable)
