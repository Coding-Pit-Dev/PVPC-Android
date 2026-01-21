package com.codingpit.pvpcplanner.domain.error

import io.mockk.every
import io.mockk.mockk
import retrofit2.HttpException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.lang.RuntimeException

class DefaultErrorHandlerTest {
    
    private val errorHandler = DefaultErrorHandler()
    
    @Test
    fun `handleError returns UnknownError for RuntimeException`() {
        val runtimeException = RuntimeException("Test error")
        
        val result = errorHandler.handleError(runtimeException)
        
        assertTrue(result is ErrorResult.UnknownError)
        assertEquals("Test error", result.message)
    }
    
    @Test
    fun `handleError returns NetworkError for IOException`() {
        val ioException = IOException("Connection timeout")
        
        val result = errorHandler.handleError(ioException)
        
        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Connection timeout - please check your internet connection", result.message)
    }
    
    @Test
    fun `handleError returns DataError for NoSuchElementException with context`() {
        val noSuchElementException = NoSuchElementException("Element not found")
        
        val result = errorHandler.handleError(noSuchElementException, "price_data")
        
        assertTrue(result is ErrorResult.DataError)
        assertEquals("No price data available for the selected date", result.message)
        assertEquals("price_data", result.context)
    }
    
    @Test
    fun `handleError returns ValidationError for IllegalArgumentException`() {
        val illegalArgumentException = IllegalArgumentException("Invalid argument")
        
        val result = errorHandler.handleError(illegalArgumentException)
        
        assertTrue(result is ErrorResult.ValidationError)
        assertEquals("Invalid argument", result.message)
    }
    
    @Test
    fun `handleError returns UnknownError for unknown exception`() {
        val unknownException = RuntimeException("Unknown error")
        
        val result = errorHandler.handleError(unknownException, "test_context")
        
        assertTrue(result is ErrorResult.UnknownError)
        assertEquals("Unknown error", result.message)
        assertEquals("test_context", result.context)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 400`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 400
        every { httpException.message } returns "Bad Request"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Bad request - please check your input", result.message)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 401`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 401
        every { httpException.message } returns "Unauthorized"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Authentication required", result.message)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 404`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 404
        every { httpException.message } returns "Not Found"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Data not found", result.message)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 429`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 429
        every { httpException.message } returns "Too Many Requests"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Too many requests - please try again later", result.message)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 500`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 500
        every { httpException.message } returns "Internal Server Error"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Server error - please try again later", result.message)
    }
}