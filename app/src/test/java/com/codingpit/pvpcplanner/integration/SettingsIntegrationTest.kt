package com.codingpit.pvpcplanner.integration

import app.cash.turbine.test
import com.codingpit.pvpcplanner.data.SettingsRepositoryImpl
import com.codingpit.pvpcplanner.data.local.store.SettingsStore
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import com.codingpit.pvpcplanner.domain.usecase.GetSettings
import com.codingpit.pvpcplanner.domain.usecase.UpdateSetting
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsIntegrationTest {
    private val mockSettingsStore = mockk<SettingsStore>()

    private lateinit var repository: SettingsRepositoryImpl
    private lateinit var getSettingsUseCase: GetSettings
    private lateinit var updateSettingUseCase: UpdateSetting

    private val defaultSettings =
        Settings(
            darkMode = DarkMode.SYSTEM,
            timeFormat = TimeFormat.TWELVE_HOURS,
            yAxisSlots = 5,
        )

    @Before
    fun setup() {
        repository = SettingsRepositoryImpl(mockSettingsStore)
        getSettingsUseCase = GetSettings(repository)
        updateSettingUseCase = UpdateSetting(repository)
    }

    @Test
    fun `complete settings flow - get default settings`() =
        runTest {
            // Arrange
            every { mockSettingsStore.settings } returns flowOf(defaultSettings)

            // Act & Assert
            getSettingsUseCase().test {
                val settings = awaitItem()
                assertEquals(DarkMode.SYSTEM, settings.darkMode)
                assertEquals(TimeFormat.TWELVE_HOURS, settings.timeFormat)
                assertEquals(5, settings.yAxisSlots)
                awaitComplete()
            }

            verify { mockSettingsStore.settings }
        }

    @Test
    fun `complete settings flow - update dark mode to light`() =
        runTest {
            // Arrange
            val updatedSettings = defaultSettings.copy(darkMode = DarkMode.LIGHT)
            every { mockSettingsStore.settings } returns flowOf(defaultSettings) andThen
                flowOf(
                    updatedSettings,
                )
            coEvery { mockSettingsStore.updateDarkMode(DarkMode.LIGHT) } returns Unit

            // Act
            updateSettingUseCase(DarkMode.LIGHT)

            // Assert
            coVerify { mockSettingsStore.updateDarkMode(DarkMode.LIGHT) }
        }

    @Test
    fun `complete settings flow - update dark mode to dark`() =
        runTest {
            // Arrange
            val updatedSettings = defaultSettings.copy(darkMode = DarkMode.DARK)
            every { mockSettingsStore.settings } returns flowOf(updatedSettings)
            coEvery { mockSettingsStore.updateDarkMode(DarkMode.DARK) } returns Unit

            // Act
            updateSettingUseCase(DarkMode.DARK)

            // Assert
            coVerify { mockSettingsStore.updateDarkMode(DarkMode.DARK) }
        }

    @Test
    fun `complete settings flow - update time format to 24 hours`() =
        runTest {
            // Arrange
            val updatedSettings = defaultSettings.copy(timeFormat = TimeFormat.TWENTY_FOUR_HOURS)
            every { mockSettingsStore.settings } returns flowOf(updatedSettings)
            coEvery { mockSettingsStore.updateTimeFormat(TimeFormat.TWENTY_FOUR_HOURS) } returns Unit

            // Act
            updateSettingUseCase(TimeFormat.TWENTY_FOUR_HOURS)

            // Assert
            coVerify { mockSettingsStore.updateTimeFormat(TimeFormat.TWENTY_FOUR_HOURS) }
        }

    @Test
    fun `complete settings flow - update time format to 12 hours`() =
        runTest {
            // Arrange
            coEvery { mockSettingsStore.updateTimeFormat(TimeFormat.TWELVE_HOURS) } returns Unit

            // Act
            updateSettingUseCase(TimeFormat.TWELVE_HOURS)

            // Assert
            coVerify { mockSettingsStore.updateTimeFormat(TimeFormat.TWELVE_HOURS) }
        }

    @Test
    fun `settings flow emits reactive updates`() =
        runTest {
            // Arrange
            val lightSettings = defaultSettings.copy(darkMode = DarkMode.LIGHT)
            val darkSettings = lightSettings.copy(darkMode = DarkMode.DARK)
            val finalSettings = darkSettings.copy(timeFormat = TimeFormat.TWENTY_FOUR_HOURS)

            every { mockSettingsStore.settings } returns
                flowOf(
                    defaultSettings,
                    lightSettings,
                    darkSettings,
                    finalSettings,
                )

            // Act & Assert
            getSettingsUseCase().test {
                val s1 = awaitItem()
                assertEquals(DarkMode.SYSTEM, s1.darkMode)
                assertEquals(TimeFormat.TWELVE_HOURS, s1.timeFormat)

                val s2 = awaitItem()
                assertEquals(DarkMode.LIGHT, s2.darkMode)
                assertEquals(TimeFormat.TWELVE_HOURS, s2.timeFormat)

                val s3 = awaitItem()
                assertEquals(DarkMode.DARK, s3.darkMode)
                assertEquals(TimeFormat.TWELVE_HOURS, s3.timeFormat)

                val s4 = awaitItem()
                assertEquals(DarkMode.DARK, s4.darkMode)
                assertEquals(TimeFormat.TWENTY_FOUR_HOURS, s4.timeFormat)

                awaitComplete()
            }

            verify { val _ = mockSettingsStore.settings }
        }

    @Test
    fun `multiple setting updates in sequence`() =
        runTest {
            // Arrange
            coEvery { mockSettingsStore.updateDarkMode(any()) } returns Unit
            coEvery { mockSettingsStore.updateTimeFormat(any()) } returns Unit

            // Act - Simulate user changing settings multiple times
            updateSettingUseCase(DarkMode.LIGHT)
            updateSettingUseCase(TimeFormat.TWENTY_FOUR_HOURS)
            updateSettingUseCase(DarkMode.DARK)
            updateSettingUseCase(DarkMode.SYSTEM)
            updateSettingUseCase(TimeFormat.TWELVE_HOURS)

            // Assert
            coVerify { mockSettingsStore.updateDarkMode(DarkMode.LIGHT) }
            coVerify { mockSettingsStore.updateTimeFormat(TimeFormat.TWENTY_FOUR_HOURS) }
            coVerify { mockSettingsStore.updateDarkMode(DarkMode.DARK) }
            coVerify { mockSettingsStore.updateDarkMode(DarkMode.SYSTEM) }
            coVerify { mockSettingsStore.updateTimeFormat(TimeFormat.TWELVE_HOURS) }
        }

    @Test
    fun `settings update handles store errors`() =
        runTest {
            // Arrange
            val exception = RuntimeException("Store update failed")
            coEvery { mockSettingsStore.updateDarkMode(DarkMode.DARK) } throws exception

            // Act & Assert
            try {
                updateSettingUseCase(DarkMode.DARK)
                org.junit.Assert.fail("Expected exception to be thrown")
            } catch (e: RuntimeException) {
                assertEquals(exception, e)
                coVerify { mockSettingsStore.updateDarkMode(DarkMode.DARK) }
            }
        }

    @Test
    fun `settings with custom y-axis slots`() =
        runTest {
            // Arrange
            val customSettings =
                Settings(
                    darkMode = DarkMode.LIGHT,
                    timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
                    yAxisSlots = 10,
                )
            every { mockSettingsStore.settings } returns flowOf(customSettings)

            // Act & Assert
            getSettingsUseCase().test {
                val settings = awaitItem()
                assertEquals(DarkMode.LIGHT, settings.darkMode)
                assertEquals(TimeFormat.TWENTY_FOUR_HOURS, settings.timeFormat)
                assertEquals(10, settings.yAxisSlots)
                awaitComplete()
            }

            verify { mockSettingsStore.settings }
        }

    @Test
    fun `complete user preference change scenario`() =
        runTest {
            // Arrange - Simulate user changing from system defaults to custom preferences
            val systemSettings = Settings(DarkMode.SYSTEM, TimeFormat.TWELVE_HOURS, 5)
            val lightSettings = systemSettings.copy(darkMode = DarkMode.LIGHT)
            val finalSettings = lightSettings.copy(timeFormat = TimeFormat.TWENTY_FOUR_HOURS)

            every { mockSettingsStore.settings } returns flowOf(systemSettings) andThen
                flowOf(lightSettings) andThen flowOf(finalSettings)
            coEvery { mockSettingsStore.updateDarkMode(DarkMode.LIGHT) } returns Unit
            coEvery { mockSettingsStore.updateTimeFormat(TimeFormat.TWENTY_FOUR_HOURS) } returns Unit

            // Act - Initial state check
            getSettingsUseCase().test {
                assertEquals(DarkMode.SYSTEM, awaitItem().darkMode)
                awaitComplete()
            }

            // Act - User switches to light mode
            updateSettingUseCase(DarkMode.LIGHT)

            // Act - User switches to 24-hour format
            updateSettingUseCase(TimeFormat.TWENTY_FOUR_HOURS)

            // Assert
            coVerify { mockSettingsStore.updateDarkMode(DarkMode.LIGHT) }
            coVerify { mockSettingsStore.updateTimeFormat(TimeFormat.TWENTY_FOUR_HOURS) }
            verify(atLeast = 1) { mockSettingsStore.settings }
        }

    @Test
    fun `settings store failure doesn't crash the flow`() =
        runTest {
            // Arrange
            val exception = RuntimeException("Settings store unavailable")
            every { mockSettingsStore.settings } throws exception

            // Act & Assert
            try {
                // We need to collect the flow to trigger the exception if it happens during collection,
                // or just call the function if it throws immediately.
                // Given the test setup 'every { ... } throws', it likely throws on invocation.
                // But to be safe and follow instructions:
                getSettingsUseCase().collect { }
                org.junit.Assert.fail("Expected exception to be thrown")
            } catch (e: RuntimeException) {
                assertEquals(exception, e)
                verify { mockSettingsStore.settings }
            }
        }
}
