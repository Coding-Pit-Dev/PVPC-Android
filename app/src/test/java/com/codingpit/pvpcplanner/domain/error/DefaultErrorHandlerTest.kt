package com.codingpit.pvpcplanner.domain.error

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

class DefaultErrorHandlerTest {

    private val errorMessageProvider = mockk<ErrorMessageProvider>()
    private val errorHandler = DefaultErrorHandler(errorMessageProvider)

    @Before
    fun setup() {
        every { errorMessageProvider.getGenericErrorMessage() } returns "Generic Error"
        every { errorMessageProvider.getTimeoutErrorMessage() } returns "Timeout Error"
        every { errorMessageProvider.getNetworkErrorMessage(any()) } returns "Network Error"
        every { errorMessageProvider.getNetworkErrorMessage() } returns "Network Error"
        every { errorMessageProvider.getDataErrorMessage(any()) } returns "Data Error"
        every { errorMessageProvider.getValidationErrorMessage(any()) } returns "Validation Error"
    }

    @Test
    fun `handleError returns UnknownError for RuntimeException`() {
        val runtimeException = RuntimeException("Test error")

        val result = errorHandler.handleError(runtimeException)

        assertTrue(result is ErrorResult.UnknownError)
        assertEquals("Generic Error", result.message)
    }

    @Test
    fun `handleError returns NetworkError with timeout message for SocketTimeoutException`() {
        val socketTimeoutException = java.net.SocketTimeoutException("timeout")

        val result = errorHandler.handleError(socketTimeoutException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Timeout Error", result.message)
    }

    @Test
    fun `handleError returns NetworkError with timeout message for ConnectException`() {
        val connectException = java.net.ConnectException("Connection refused")

        val result = errorHandler.handleError(connectException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Timeout Error", result.message)
    }

    @Test
    fun `handleError returns NetworkError with generic message for IOException without timeout`() {
        val ioException = IOException("Connection refused")

        val result = errorHandler.handleError(ioException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Network Error", result.message)
    }

    @Test
    fun `handleError returns DataError for NoSuchElementException with context`() {
        val noSuchElementException = NoSuchElementException("Element not found")
        every { errorMessageProvider.getDataErrorMessage("price_data") } returns "Data Error for price_data"

        val result = errorHandler.handleError(noSuchElementException, "price_data")

        assertTrue(result is ErrorResult.DataError)
        assertEquals("Data Error for price_data", result.message)
        assertEquals("price_data", result.context)
    }

    @Test
    fun `handleError returns ValidationError for IllegalArgumentException`() {
        val illegalArgumentException = IllegalArgumentException("Invalid argument")
        every { errorMessageProvider.getValidationErrorMessage(null) } returns "Validation Error"

        val result = errorHandler.handleError(illegalArgumentException)

        assertTrue(result is ErrorResult.ValidationError)
        assertEquals("Validation Error", result.message)
    }

    @Test
    fun `handleError returns UnknownError for unknown exception`() {
        val unknownException = RuntimeException("Unknown error")

        val result = errorHandler.handleError(unknownException, "test_context")

        assertTrue(result is ErrorResult.UnknownError)
        assertEquals("Generic Error", result.message)
        assertEquals("test_context", result.context)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 400`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 400
        every { httpException.message } returns "Bad Request"
        every { errorMessageProvider.getNetworkErrorMessage(400) } returns "Network Error 400"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Network Error 400", result.message)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 401`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 401
        every { httpException.message } returns "Unauthorized"
        every { errorMessageProvider.getNetworkErrorMessage(401) } returns "Network Error 401"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Network Error 401", result.message)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 404`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 404
        every { httpException.message } returns "Not Found"
        every { errorMessageProvider.getNetworkErrorMessage(404) } returns "Network Error 404"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Network Error 404", result.message)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 429`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 429
        every { httpException.message } returns "Too Many Requests"
        every { errorMessageProvider.getNetworkErrorMessage(429) } returns "Network Error 429"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Network Error 429", result.message)
    }

    @Test
    fun `handleError returns NetworkError for HttpException 500`() {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 500
        every { httpException.message } returns "Internal Server Error"
        every { errorMessageProvider.getNetworkErrorMessage(500) } returns "Network Error 500"

        val result = errorHandler.handleError(httpException)

        assertTrue(result is ErrorResult.NetworkError)
        assertEquals("Network Error 500", result.message)
    }

    @Test(expected = CancellationException::class)
    fun `handleError rethrows CancellationException`() {
        val cancellationException = CancellationException("Cancelled")

        errorHandler.handleError(cancellationException)

        // Should throw, not return ErrorResult
    }
}
