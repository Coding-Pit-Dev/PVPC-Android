package com.codingpit.pvpcplanner.domain.error

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ErrorStateManagerTest {

    data class TestState(val data: String) {
        companion object Factory : HasErrorState<TestState> {
            override fun createErrorState(errorResult: ErrorResult): TestState {
                return TestState("ERROR: ${errorResult.message}")
            }
        }
    }

    private val errorMessageProvider = mockk<ErrorMessageProvider>()
    private val errorHandler = DefaultErrorHandler(errorMessageProvider)

    @Before
    fun setup() {
        every { errorMessageProvider.getGenericErrorMessage() } returns "Generic Error"
    }

    @Test
    fun `handleErrors extension function catches exceptions and creates error state`() = runTest {
        // Arrange
        val testFlow = flowOf(TestState("test"))
            .handleErrors(errorHandler, "test_context", TestState.Factory)

        // Act & Assert
        testFlow.test {
            assertEquals("test", awaitItem().data)
            awaitComplete()
        }
    }

    @Test
    fun `handleErrors creates error state when exception occurs`() = runTest {
        // Arrange
        val errorFlow = flow<TestState> { throw RuntimeException("Test error") }
            .handleErrors(errorHandler, "test_context", TestState.Factory)

        // Act & Assert
        errorFlow.test {
            val item = awaitItem()
            assertEquals("ERROR: Generic Error", item.data)
            awaitComplete()
        }
    }

    @Test
    fun `handleResultErrors catches exceptions and emits Result failure`() = runTest {
        // Arrange
        val errorFlow = flow<Result<String>> { throw RuntimeException("Test error") }
            .handleResultErrors(errorHandler, "test_context")

        // Act & Assert
        errorFlow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue(exception is RuntimeException)
            assertEquals("Generic Error", exception?.message)
            assertTrue(exception?.cause is RuntimeException)
            assertEquals("Test error", exception?.cause?.message)
            awaitComplete()
        }
    }

    @Test
    fun `handleResultErrors preserves successful results`() = runTest {
        // Arrange
        val successFlow = flowOf(Result.success("success"))
            .handleResultErrors(errorHandler, "test_context")

        // Act & Assert
        successFlow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals("success", result.getOrNull())
            awaitComplete()
        }
    }
}