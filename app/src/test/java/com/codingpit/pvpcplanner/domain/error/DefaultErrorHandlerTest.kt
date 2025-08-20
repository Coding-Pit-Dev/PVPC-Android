package com.codingpit.pvpcplanner.domain.error

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
}