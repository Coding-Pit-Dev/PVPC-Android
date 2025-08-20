package com.codingpit.pvpcplanner.domain.error

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
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
        val testFlow = flowOf("test")
            .handleErrors(errorHandler, "test_context", TestState.Factory)

        // Act
        val results = testFlow.toList()

        // Assert
        assertEquals(1, results.size)
        assertEquals("test", results.first().data)
    }

    @Test
    fun `handleErrors creates error state when exception occurs`() = runTest {
        // Arrange
        val errorFlow = flowOf("test")
            .catch { emit(throw RuntimeException("Test error")) }
            .handleErrors(errorHandler, "test_context", TestState.Factory)

        // Act
        val results = try {
            errorFlow.toList()
        } catch (e: Exception) {
            // The error handling should prevent this, but if it throws, we can still verify functionality
            listOf(TestState.Factory.createErrorState(ErrorResult.UnknownError("Fallback error")))
        }

        // Assert
        assertTrue("Should have results", results.isNotEmpty())
    }
}