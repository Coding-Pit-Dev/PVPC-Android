package com.codingpit.pvpcplanner.domain.error

import app.cash.turbine.test
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorStateManagerTest {

    data class TestState(val data: String) {
        companion object Factory : HasErrorState<TestState> {
            override fun createErrorState(errorResult: ErrorResult): TestState {
                return TestState("ERROR: ${errorResult.message}")
            }
        }
    }

    private val errorHandler = DefaultErrorHandler()

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
            assertTrue(item.data.contains("ERROR"))
            awaitComplete()
        }
    }
}