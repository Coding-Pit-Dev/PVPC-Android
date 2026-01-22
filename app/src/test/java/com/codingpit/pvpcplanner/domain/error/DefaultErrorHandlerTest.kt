package com.codingpit.pvpcplanner.domain.error

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

class DefaultErrorHandlerTest {

    private val errorMessageProvider = DefaultErrorMessageProvider()
    private val errorHandler = DefaultErrorHandler(errorMessageProvider)

    @Test
    fun `handleError returns UnknownError for RuntimeException`() {
        val runtimeException = RuntimeException("Test error")

        val result = errorHandler.handleError(runtimeException)

        assertTrue(result is ErrorResult.UnknownError)
        assertEquals(errorMessageProvider.getGenericErrorMessage(), result.message)
    }

    @Test
    fun `handleError returns NetworkError with timeout message for SocketTimeoutException`() {
        val socketTimeoutException = java.net.SocketTimeoutException("timeout")

        val result = errorHandler.handleError(socketTimeoutException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals(errorMessageProvider.getTimeoutErrorMessage(), result.message)
    }

    @Test
    fun `handleError returns NetworkError with timeout message for ConnectException`() {
        val connectException = java.net.ConnectException("Connection refused")

        val result = errorHandler.handleError(connectException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals(errorMessageProvider.getTimeoutErrorMessage(), result.message)
    }

    @Test
    fun `handleError returns NetworkError with generic message for IOException without timeout`() {
        val ioException = IOException("Connection refused")

        val result = errorHandler.handleError(ioException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals(errorMessageProvider.getNetworkErrorMessage(), result.message)
    }

    @Test
    fun `handleError returns DataError for NoSuchElementException with context`() {
        val noSuchElementException = NoSuchElementException("Element not found")

        val result = errorHandler.handleError(noSuchElementException, "price_data")

        assertTrue(result is ErrorResult.DataError)
        assertEquals(errorMessageProvider.getDataErrorMessage("price_data"), result.message)
        assertEquals("price_data", result.context)
    }

    @Test
    fun `handleError returns ValidationError for IllegalArgumentException`() {
        val illegalArgumentException = IllegalArgumentException("Invalid argument")

        val result = errorHandler.handleError(illegalArgumentException)

        assertTrue(result is ErrorResult.ValidationError)
        assertEquals(errorMessageProvider.getValidationErrorMessage(null), result.message)
    }

    @Test
    fun `handleError returns UnknownError for unknown exception`() {
        val unknownException = RuntimeException("Unknown error")

        val result = errorHandler.handleError(unknownException, "test_context")

        assertTrue(result is ErrorResult.UnknownError)
        assertEquals(errorMessageProvider.getGenericErrorMessage(), result.message)
        assertEquals("test_context", result.context)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 400`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 400
        every { httpException.message } returns "Bad Request"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals(errorMessageProvider.getNetworkErrorMessage(400), result.message)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 401`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 401
        every { httpException.message } returns "Unauthorized"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals(errorMessageProvider.getNetworkErrorMessage(401), result.message)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 404`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 404
        every { httpException.message } returns "Not Found"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals(errorMessageProvider.getNetworkErrorMessage(404), result.message)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 429`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 429
        every { httpException.message } returns "Too Many Requests"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals(errorMessageProvider.getNetworkErrorMessage(429), result.message)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 500`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 500
        every { httpException.message } returns "Internal Server Error"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals(errorMessageProvider.getNetworkErrorMessage(500), result.message)
    }

    @Test(expected = CancellationException::class)
    fun `handleError rethrows CancellationException`() {
        val cancellationException = CancellationException("Cancelled")

        errorHandler.handleError(cancellationException)

        // Should throw, not return ErrorResult
    }
}
