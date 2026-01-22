package com.codingpit.pvpcplanner.domain.error

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

interface ErrorState {
    val error: String
}

interface HasErrorState<T> {
    fun createErrorState(errorResult: ErrorResult): T
}

fun <R : Any> Flow<R>.handleErrors(
    errorHandler: ErrorHandler,
    context: String? = null,
    stateFactory: HasErrorState<R>
): Flow<R> {
    return this.catch { throwable ->
            val errorResult = errorHandler.handleError(throwable, context)
            emit(stateFactory.createErrorState(errorResult))
        }
}

fun <T> Flow<Result<T>>.handleResultErrors(
    errorHandler: ErrorHandler,
    context: String? = null
): Flow<Result<T>> {
    return this.catch { throwable ->
        val errorResult = errorHandler.handleError(throwable, context)
        emit(Result.failure(RuntimeException(errorResult.message, throwable)))
    }
}