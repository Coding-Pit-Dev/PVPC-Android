package com.codingpit.pvpcplanner.integration

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
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsIntegrationTest {

    private val mockSettingsStore = mockk<SettingsStore>()
    
    private lateinit var repository: SettingsRepositoryImpl
    private lateinit var getSettingsUseCase: GetSettings
    private lateinit var updateSettingUseCase: UpdateSetting

    private val defaultSettings = Settings(
        darkMode = DarkMode.SYSTEM,
        timeFormat = TimeFormat.TWELVE_HOURS,
        yAxisSlots = 5
    )

    @Before
    fun setup() {
        repository = SettingsRepositoryImpl(mockSettingsStore)
        getSettingsUseCase = GetSettings(repository)
        updateSettingUseCase = UpdateSetting(repository)
    }

    @Test
    fun `complete settings flow - get default settings`() = runTest {
        // Arrange
        every { mockSettingsStore.settings } returns flowOf(defaultSettings)

        // Act
        val settingsFlow = getSettingsUseCase()
        val emissions = settingsFlow.toList()

        // Assert
        assertEquals(1, emissions.size)
        val settings = emissions[0]
        assertEquals(DarkMode.SYSTEM, settings.darkMode)
        assertEquals(TimeFormat.TWELVE_HOURS, settings.timeFormat)
        assertEquals(5, settings.yAxisSlots)
        
        verify { mockSettingsStore.settings }
    }

    @Test
    fun `complete settings flow - update dark mode to light`() = runTest {
        // Arrange
        val updatedSettings = defaultSettings.copy(darkMode = DarkMode.LIGHT)
        every { mockSettingsStore.settings } returns flowOf(defaultSettings) andThen flowOf(updatedSettings)
        coEvery { mockSettingsStore.updateDarkMode(DarkMode.LIGHT) } returns Unit

        // Act
        updateSettingUseCase(DarkMode.LIGHT)

        // Assert
        coVerify { mockSettingsStore.updateDarkMode(DarkMode.LIGHT) }
    }

    @Test
    fun `complete settings flow - update dark mode to dark`() = runTest {
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
    fun `complete settings flow - update time format to 24 hours`() = runTest {
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
    fun `complete settings flow - update time format to 12 hours`() = runTest {
        // Arrange
        coEvery { mockSettingsStore.updateTimeFormat(TimeFormat.TWELVE_HOURS) } returns Unit

        // Act
        updateSettingUseCase(TimeFormat.TWELVE_HOURS)

        // Assert
        coVerify { mockSettingsStore.updateTimeFormat(TimeFormat.TWELVE_HOURS) }
    }

    @Test
    fun `settings flow emits reactive updates`() = runTest {
        // Arrange
        val lightSettings = defaultSettings.copy(darkMode = DarkMode.LIGHT)
        val darkSettings = lightSettings.copy(darkMode = DarkMode.DARK)
        val finalSettings = darkSettings.copy(timeFormat = TimeFormat.TWENTY_FOUR_HOURS)
        
        every { mockSettingsStore.settings } returns flowOf(
            defaultSettings,
            lightSettings, 
            darkSettings,
            finalSettings
        )

        // Act
        val settingsFlow = getSettingsUseCase()
        val emissions = settingsFlow.toList()

        // Assert
        assertEquals(4, emissions.size)
        
        assertEquals(DarkMode.SYSTEM, emissions[0].darkMode)
        assertEquals(TimeFormat.TWELVE_HOURS, emissions[0].timeFormat)
        
        assertEquals(DarkMode.LIGHT, emissions[1].darkMode)
        assertEquals(TimeFormat.TWELVE_HOURS, emissions[1].timeFormat)
        
        assertEquals(DarkMode.DARK, emissions[2].darkMode)
        assertEquals(TimeFormat.TWELVE_HOURS, emissions[2].timeFormat)
        
        assertEquals(DarkMode.DARK, emissions[3].darkMode)
        assertEquals(TimeFormat.TWENTY_FOUR_HOURS, emissions[3].timeFormat)
        
        verify { mockSettingsStore.settings }
    }

    @Test
    fun `multiple setting updates in sequence`() = runTest {
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
    fun `settings update handles store errors`() = runTest {
        // Arrange
        val exception = RuntimeException("Store update failed")
        coEvery { mockSettingsStore.updateDarkMode(DarkMode.DARK) } throws exception

        // Act & Assert
        try {
            updateSettingUseCase(DarkMode.DARK)
            assert(false) { "Expected exception to be thrown" }
        } catch (e: RuntimeException) {
            assertEquals(exception, e)
            coVerify { mockSettingsStore.updateDarkMode(DarkMode.DARK) }
        }
    }

    @Test
    fun `settings with custom y-axis slots`() = runTest {
        // Arrange
        val customSettings = Settings(
            darkMode = DarkMode.LIGHT,
            timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
            yAxisSlots = 10
        )
        every { mockSettingsStore.settings } returns flowOf(customSettings)

        // Act
        val settingsFlow = getSettingsUseCase()
        val result = settingsFlow.toList()

        // Assert
        assertEquals(1, result.size)
        val settings = result[0]
        assertEquals(DarkMode.LIGHT, settings.darkMode)
        assertEquals(TimeFormat.TWENTY_FOUR_HOURS, settings.timeFormat)
        assertEquals(10, settings.yAxisSlots)
        
        verify { mockSettingsStore.settings }
    }

    @Test
    fun `complete user preference change scenario`() = runTest {
        // Arrange - Simulate user changing from system defaults to custom preferences
        val systemSettings = Settings(DarkMode.SYSTEM, TimeFormat.TWELVE_HOURS, 5)
        val lightSettings = systemSettings.copy(darkMode = DarkMode.LIGHT)
        val finalSettings = lightSettings.copy(timeFormat = TimeFormat.TWENTY_FOUR_HOURS)
        
        every { mockSettingsStore.settings } returns flowOf(systemSettings) andThen 
            flowOf(lightSettings) andThen flowOf(finalSettings)
        coEvery { mockSettingsStore.updateDarkMode(DarkMode.LIGHT) } returns Unit
        coEvery { mockSettingsStore.updateTimeFormat(TimeFormat.TWENTY_FOUR_HOURS) } returns Unit

        // Act - Initial state check
        val initialFlow = getSettingsUseCase()
        val initialResult = initialFlow.toList()
        assertEquals(DarkMode.SYSTEM, initialResult[0].darkMode)

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
    fun `settings store failure doesn't crash the flow`() = runTest {
        // Arrange
        val exception = RuntimeException("Settings store unavailable")
        every { mockSettingsStore.settings } throws exception

        // Act & Assert
        try {
            val settingsFlow = getSettingsUseCase()
            settingsFlow.toList()
            assert(false) { "Expected exception to be thrown" }
        } catch (e: RuntimeException) {
            assertEquals(exception, e)
            verify { mockSettingsStore.settings }
        }
    }
}
