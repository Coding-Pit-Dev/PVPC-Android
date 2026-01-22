package com.codingpit.pvpcplanner.domain.error

interface ErrorHandler {
    fun handleError(throwable: Throwable): ErrorResult
    fun handleError(throwable: Throwable, context: String?): ErrorResult
}

sealed class ErrorResult {
    abstract val message: String
    abstract val context: String?
    
    data class NetworkError(
        override val message: String,
        override val context: String? = null
    ) : ErrorResult()
    
    data class DataError(
        override val message: String,
        override val context: String? = null
    ) : ErrorResult()
    
    data class ValidationError(
        override val message: String,
        override val context: String? = null
    ) : ErrorResult()
    
    data class UnknownError(
        override val message: String,
        override val context: String? = null
    ) : ErrorResult()
}