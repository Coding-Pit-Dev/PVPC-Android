package com.codingpit.pvpcplanner.domain.error

import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DefaultErrorHandler @Inject constructor() : ErrorHandler {
    
    override fun handleError(throwable: Throwable): ErrorResult {
        return handleError(throwable, null)
    }
    
    override fun handleError(throwable: Throwable, context: String?): ErrorResult {
        // Log error (avoid Log.e in unit tests)
        println("ErrorHandler: Error in context: $context - ${throwable.message}")
        
        return when (throwable) {
            is HttpException -> handleHttpException(throwable, context)
            is IOException -> handleNetworkException(throwable, context)
            is NoSuchElementException -> handleDataException(throwable, context)
            is IllegalArgumentException -> handleValidationException(throwable, context)
            else -> ErrorResult.UnknownError(
                message = throwable.message ?: "An unexpected error occurred",
                context = context
            )
        }
    }
    
    private fun handleHttpException(exception: HttpException, context: String?): ErrorResult {
        val message = when (exception.code()) {
            400 -> "Bad request - please check your input"
            401 -> "Authentication required"
            403 -> "Access denied"
            404 -> "Data not found"
            429 -> "Too many requests - please try again later"
            500 -> "Server error - please try again later"
            503 -> "Service unavailable - please try again later"
            else -> "Network error (${exception.code()})"
        }
        
        return ErrorResult.NetworkError(message, context)
    }
    
    private fun handleNetworkException(exception: IOException, context: String?): ErrorResult {
        val message = when {
            exception.message?.contains("timeout", ignoreCase = true) == true -> 
                "Connection timeout - please check your internet connection"
            exception.message?.contains("network", ignoreCase = true) == true -> 
                "Network error - please check your connection"
            else -> "Connection error - please try again"
        }
        
        return ErrorResult.NetworkError(message, context)
    }
    
    private fun handleDataException(exception: NoSuchElementException, context: String?): ErrorResult {
        val message = when (context) {
            "price_data" -> "No price data available for the selected date"
            "device_data" -> "Device information not found"
            "current_hour" -> "Current hour price data not available"
            else -> "Required data not found"
        }
        
        return ErrorResult.DataError(message, context)
    }
    
    private fun handleValidationException(exception: IllegalArgumentException, context: String?): ErrorResult {
        return ErrorResult.ValidationError(
            message = exception.message ?: "Invalid input provided",
            context = context
        )
    }
}