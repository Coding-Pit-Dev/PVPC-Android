package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.SettingsRepository
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UpdateSettingTest {
    private val mockRepository = mockk<SettingsRepository>()
    private lateinit var useCase: UpdateSetting

    @Before
    fun setup() {
        useCase = UpdateSetting(mockRepository)
    }

    @Test
    fun `invoke updates dark mode to LIGHT`() =
        runTest {
            // Arrange
            val darkMode = DarkMode.LIGHT
            coEvery { mockRepository.updateDarkMode(darkMode) } returns Unit

            // Act
            useCase(darkMode)

            // Assert
            coVerify { mockRepository.updateDarkMode(darkMode) }
        }

    @Test
    fun `invoke updates dark mode to DARK`() =
        runTest {
            // Arrange
            val darkMode = DarkMode.DARK
            coEvery { mockRepository.updateDarkMode(darkMode) } returns Unit

            // Act
            useCase(darkMode)

            // Assert
            coVerify { mockRepository.updateDarkMode(darkMode) }
        }

    @Test
    fun `invoke updates dark mode to SYSTEM`() =
        runTest {
            // Arrange
            val darkMode = DarkMode.SYSTEM
            coEvery { mockRepository.updateDarkMode(darkMode) } returns Unit

            // Act
            useCase(darkMode)

            // Assert
            coVerify { mockRepository.updateDarkMode(darkMode) }
        }

    @Test
    fun `invoke updates time format to TWELVE_HOURS`() =
        runTest {
            // Arrange
            val timeFormat = TimeFormat.TWELVE_HOURS
            coEvery { mockRepository.updateTimeFormat(timeFormat) } returns Unit

            // Act
            useCase(timeFormat)

            // Assert
            coVerify { mockRepository.updateTimeFormat(timeFormat) }
        }

    @Test
    fun `invoke updates time format to TWENTY_FOUR_HOURS`() =
        runTest {
            // Arrange
            val timeFormat = TimeFormat.TWENTY_FOUR_HOURS
            coEvery { mockRepository.updateTimeFormat(timeFormat) } returns Unit

            // Act
            useCase(timeFormat)

            // Assert
            coVerify { mockRepository.updateTimeFormat(timeFormat) }
        }

    @Test
    fun `invoke propagates dark mode update exception`() =
        runTest {
            // Arrange
            val darkMode = DarkMode.DARK
            val exception = RuntimeException("Failed to update dark mode setting")
            coEvery { mockRepository.updateDarkMode(darkMode) } throws exception

            // Act & Assert
            try {
                useCase(darkMode)
                org.junit.Assert.fail("Expected RuntimeException")
            } catch (e: RuntimeException) {
                assertEquals(exception, e)
            }
            coVerify { mockRepository.updateDarkMode(darkMode) }
        }

    @Test
    fun `invoke propagates time format update exception`() =
        runTest {
            // Arrange
            val timeFormat = TimeFormat.TWENTY_FOUR_HOURS
            val exception = RuntimeException("Failed to update time format setting")
            coEvery { mockRepository.updateTimeFormat(timeFormat) } throws exception

            // Act & Assert
            try {
                useCase(timeFormat)
                org.junit.Assert.fail("Expected RuntimeException")
            } catch (e: RuntimeException) {
                assertEquals(exception, e)
            }
            coVerify { mockRepository.updateTimeFormat(timeFormat) }
        }

    @Test
    fun `invoke handles database constraint exceptions for dark mode`() =
        runTest {
            // Arrange
            val darkMode = DarkMode.LIGHT
            val exception = RuntimeException("Database constraint violation")
            coEvery { mockRepository.updateDarkMode(darkMode) } throws exception

            // Act & Assert
            try {
                useCase(darkMode)
                org.junit.Assert.fail("Expected RuntimeException")
            } catch (e: RuntimeException) {
                assertEquals(exception, e)
            }
            coVerify { mockRepository.updateDarkMode(darkMode) }
        }

    @Test
    fun `invoke handles database constraint exceptions for time format`() =
        runTest {
            // Arrange
            val timeFormat = TimeFormat.TWELVE_HOURS
            val exception = RuntimeException("Database constraint violation")
            coEvery { mockRepository.updateTimeFormat(timeFormat) } throws exception

            // Act & Assert
            try {
                useCase(timeFormat)
                org.junit.Assert.fail("Expected RuntimeException")
            } catch (e: RuntimeException) {
                assertEquals(exception, e)
            }
            coVerify { mockRepository.updateTimeFormat(timeFormat) }
        }
}
