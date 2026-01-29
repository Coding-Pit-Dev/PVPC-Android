package com.codingpit.pvpcplanner.domain.usecase

import app.cash.turbine.test
import com.codingpit.pvpcplanner.data.SettingsRepository
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetSettingsTest {
    private val mockRepository = mockk<SettingsRepository>()
    private lateinit var useCase: GetSettings

    @Before
    fun setup() {
        useCase = GetSettings(mockRepository)
    }

    @Test
    fun `invoke returns settings flow from repository`() =
        runTest {
            // Arrange
            val settings =
                Settings(
                    darkMode = DarkMode.SYSTEM,
                    timeFormat = TimeFormat.TWELVE_HOURS,
                    yAxisSlots = 5,
                )
            val settingsFlow = flowOf(settings)
            every { mockRepository.getSettings() } returns settingsFlow

            // Act
            val result = useCase()

            // Assert
            assertEquals(settingsFlow, result)
            verify { mockRepository.getSettings() }
        }

    @Test
    fun `invoke returns default settings when repository returns defaults`() =
        runTest {
            // Arrange
            val defaultSettings = Settings()
            val settingsFlow = flowOf(defaultSettings)
            every { mockRepository.getSettings() } returns settingsFlow

            // Act
            val result = useCase()

            // Assert
            result.test {
                val item = awaitItem()
                assertEquals(defaultSettings, item)
                assertEquals(DarkMode.SYSTEM, item.darkMode)
                assertEquals(TimeFormat.TWELVE_HOURS, item.timeFormat)
                assertEquals(5, item.yAxisSlots)
                awaitComplete()
            }
            verify { mockRepository.getSettings() }
        }

    @Test
    fun `invoke returns dark mode settings`() =
        runTest {
            // Arrange
            val darkSettings =
                Settings(
                    darkMode = DarkMode.DARK,
                    timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
                    yAxisSlots = 8,
                )
            val settingsFlow = flowOf(darkSettings)
            every { mockRepository.getSettings() } returns settingsFlow

            // Act
            val result = useCase()

            // Assert
            result.test {
                assertEquals(darkSettings, awaitItem())
                awaitComplete()
            }
            verify { mockRepository.getSettings() }
        }

    @Test
    fun `invoke returns light mode settings`() =
        runTest {
            // Arrange
            val lightSettings =
                Settings(
                    darkMode = DarkMode.LIGHT,
                    timeFormat = TimeFormat.TWELVE_HOURS,
                    yAxisSlots = 3,
                )
            val settingsFlow = flowOf(lightSettings)
            every { mockRepository.getSettings() } returns settingsFlow

            // Act
            val result = useCase()

            // Assert
            result.test {
                assertEquals(lightSettings, awaitItem())
                awaitComplete()
            }
            verify { mockRepository.getSettings() }
        }

    @Test
    fun `invoke handles multiple emissions from repository`() =
        runTest {
            // Arrange
            val firstSettings = Settings(darkMode = DarkMode.LIGHT)
            val secondSettings =
                Settings(darkMode = DarkMode.DARK, timeFormat = TimeFormat.TWENTY_FOUR_HOURS)
            val multiEmissionFlow = flowOf(firstSettings, secondSettings)
            every { mockRepository.getSettings() } returns multiEmissionFlow

            // Act
            val result = useCase()

            // Assert
            result.test {
                assertEquals(firstSettings, awaitItem())
                assertEquals(secondSettings, awaitItem())
                awaitComplete()
            }
            verify { mockRepository.getSettings() }
        }
}
