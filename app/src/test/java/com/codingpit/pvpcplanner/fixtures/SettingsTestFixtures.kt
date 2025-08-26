package com.codingpit.pvpcplanner.fixtures

import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat

/**
 * Test fixtures for Settings and configuration data.
 * Provides various settings configurations for testing different scenarios.
 */
object SettingsTestFixtures {
    
    /**
     * Default settings as they would appear on first app launch.
     */
    val DEFAULT_SETTINGS = Settings(
        darkMode = DarkMode.SYSTEM,
        timeFormat = TimeFormat.TWELVE_HOURS,
        yAxisSlots = 5
    )
    
    /**
     * Light mode settings for users who prefer light theme.
     */
    val LIGHT_MODE_SETTINGS = Settings(
        darkMode = DarkMode.LIGHT,
        timeFormat = TimeFormat.TWELVE_HOURS,
        yAxisSlots = 5
    )
    
    /**
     * Dark mode settings for users who prefer dark theme.
     */
    val DARK_MODE_SETTINGS = Settings(
        darkMode = DarkMode.DARK,
        timeFormat = TimeFormat.TWELVE_HOURS,
        yAxisSlots = 5
    )
    
    /**
     * European user settings (24-hour format preference).
     */
    val EUROPEAN_USER_SETTINGS = Settings(
        darkMode = DarkMode.SYSTEM,
        timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
        yAxisSlots = 5
    )
    
    /**
     * US user settings (AM/PM format preference).
     */
    val US_USER_SETTINGS = Settings(
        darkMode = DarkMode.LIGHT,
        timeFormat = TimeFormat.TWELVE_HOURS,
        yAxisSlots = 5
    )
    
    /**
     * Professional user settings (dark mode + 24h format).
     */
    val PROFESSIONAL_SETTINGS = Settings(
        darkMode = DarkMode.DARK,
        timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
        yAxisSlots = 8
    )
    
    /**
     * Accessibility settings (high contrast implied by light mode + large charts).
     */
    val ACCESSIBILITY_SETTINGS = Settings(
        darkMode = DarkMode.LIGHT,
        timeFormat = TimeFormat.TWELVE_HOURS,
        yAxisSlots = 10
    )
    
    /**
     * Minimal chart settings for clean UI preference.
     */
    val MINIMAL_CHART_SETTINGS = Settings(
        darkMode = DarkMode.SYSTEM,
        timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
        yAxisSlots = 3
    )
    
    /**
     * Detailed chart settings for power users.
     */
    val DETAILED_CHART_SETTINGS = Settings(
        darkMode = DarkMode.DARK,
        timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
        yAxisSlots = 12
    )
    
    /**
     * Edge case settings for testing boundary conditions.
     */
    object EdgeCases {
        val MAXIMUM_Y_AXIS = Settings(
            darkMode = DarkMode.SYSTEM,
            timeFormat = TimeFormat.TWELVE_HOURS,
            yAxisSlots = 20
        )
        
        val MINIMUM_Y_AXIS = Settings(
            darkMode = DarkMode.SYSTEM,
            timeFormat = TimeFormat.TWELVE_HOURS,
            yAxisSlots = 2
        )
        
        // Invalid settings that might come from corrupted storage
        val ZERO_Y_AXIS = Settings(
            darkMode = DarkMode.SYSTEM,
            timeFormat = TimeFormat.TWELVE_HOURS,
            yAxisSlots = 0
        )
        
        val NEGATIVE_Y_AXIS = Settings(
            darkMode = DarkMode.SYSTEM,
            timeFormat = TimeFormat.TWELVE_HOURS,
            yAxisSlots = -1
        )
    }
    
    /**
     * Settings transition scenarios for testing state changes.
     */
    object Transitions {
        val SYSTEM_TO_LIGHT = listOf(DEFAULT_SETTINGS, LIGHT_MODE_SETTINGS)
        val SYSTEM_TO_DARK = listOf(DEFAULT_SETTINGS, DARK_MODE_SETTINGS)
        val LIGHT_TO_DARK = listOf(LIGHT_MODE_SETTINGS, DARK_MODE_SETTINGS)
        val DARK_TO_LIGHT = listOf(DARK_MODE_SETTINGS, LIGHT_MODE_SETTINGS)
        
        val TWELVE_TO_TWENTY_FOUR = listOf(
            DEFAULT_SETTINGS,
            DEFAULT_SETTINGS.copy(timeFormat = TimeFormat.TWENTY_FOUR_HOURS)
        )
        
        val TWENTY_FOUR_TO_TWELVE = listOf(
            EUROPEAN_USER_SETTINGS,
            EUROPEAN_USER_SETTINGS.copy(timeFormat = TimeFormat.TWELVE_HOURS)
        )
        
        val COMPLETE_CUSTOMIZATION = listOf(
            DEFAULT_SETTINGS,                    // System, 12h, 5 slots
            LIGHT_MODE_SETTINGS,                // Light, 12h, 5 slots
            LIGHT_MODE_SETTINGS.copy(           // Light, 24h, 5 slots
                timeFormat = TimeFormat.TWENTY_FOUR_HOURS
            ),
            PROFESSIONAL_SETTINGS               // Dark, 24h, 8 slots
        )
    }
    
    /**
     * Common settings combinations by user type.
     */
    object UserProfiles {
        /**
         * Casual user - prefers simple, default settings.
         */
        val CASUAL_USER = DEFAULT_SETTINGS
        
        /**
         * Tech-savvy user - customizes everything.
         */
        val TECH_SAVVY_USER = Settings(
            darkMode = DarkMode.DARK,
            timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
            yAxisSlots = 10
        )
        
        /**
         * Elderly user - prefers familiar formats and simple charts.
         */
        val ELDERLY_USER = Settings(
            darkMode = DarkMode.LIGHT,
            timeFormat = TimeFormat.TWELVE_HOURS,
            yAxisSlots = 4
        )
        
        /**
         * Professional user - productivity focused.
         */
        val PROFESSIONAL_USER = PROFESSIONAL_SETTINGS
        
        /**
         * Energy analyst - needs detailed information.
         */
        val ENERGY_ANALYST = Settings(
            darkMode = DarkMode.DARK,
            timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
            yAxisSlots = 15
        )
        
        /**
         * Mobile-first user - prefers system theme and standard formats.
         */
        val MOBILE_FIRST_USER = Settings(
            darkMode = DarkMode.SYSTEM,
            timeFormat = TimeFormat.TWELVE_HOURS,
            yAxisSlots = 6
        )
    }
    
    /**
     * Settings update scenarios for testing incremental changes.
     */
    object UpdateScenarios {
        fun updateDarkMode(settings: Settings, darkMode: DarkMode) = 
            settings.copy(darkMode = darkMode)
        
        fun updateTimeFormat(settings: Settings, timeFormat: TimeFormat) = 
            settings.copy(timeFormat = timeFormat)
        
        fun updateYAxisSlots(settings: Settings, yAxisSlots: Int) = 
            settings.copy(yAxisSlots = yAxisSlots)
        
        val ENABLE_DARK_MODE = updateDarkMode(DEFAULT_SETTINGS, DarkMode.DARK)
        val ENABLE_LIGHT_MODE = updateDarkMode(DARK_MODE_SETTINGS, DarkMode.LIGHT)
        val ENABLE_24H_FORMAT = updateTimeFormat(US_USER_SETTINGS, TimeFormat.TWENTY_FOUR_HOURS)
        val ENABLE_12H_FORMAT = updateTimeFormat(EUROPEAN_USER_SETTINGS, TimeFormat.TWELVE_HOURS)
        val INCREASE_CHART_DETAIL = updateYAxisSlots(DEFAULT_SETTINGS, 8)
        val DECREASE_CHART_DETAIL = updateYAxisSlots(DETAILED_CHART_SETTINGS, 3)
    }
    
    /**
     * Settings for different device orientations and screen sizes.
     */
    object ScreenAdaptations {
        val TABLET_LANDSCAPE = Settings(
            darkMode = DarkMode.SYSTEM,
            timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
            yAxisSlots = 12
        )
        
        val PHONE_PORTRAIT = Settings(
            darkMode = DarkMode.SYSTEM,
            timeFormat = TimeFormat.TWELVE_HOURS,
            yAxisSlots = 5
        )
        
        val SMALL_SCREEN = Settings(
            darkMode = DarkMode.LIGHT,
            timeFormat = TimeFormat.TWELVE_HOURS,
            yAxisSlots = 3
        )
        
        val LARGE_SCREEN = Settings(
            darkMode = DarkMode.DARK,
            timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
            yAxisSlots = 15
        )
    }
    
    /**
     * Settings persistence test scenarios.
     */
    object PersistenceScenarios {
        val SETTINGS_SEQUENCE = listOf(
            DEFAULT_SETTINGS,
            LIGHT_MODE_SETTINGS,
            EUROPEAN_USER_SETTINGS,
            PROFESSIONAL_SETTINGS,
            ACCESSIBILITY_SETTINGS
        )
        
        val RAPID_CHANGES = listOf(
            DEFAULT_SETTINGS,
            DEFAULT_SETTINGS.copy(darkMode = DarkMode.DARK),
            DEFAULT_SETTINGS.copy(darkMode = DarkMode.LIGHT),
            DEFAULT_SETTINGS.copy(darkMode = DarkMode.SYSTEM),
            DEFAULT_SETTINGS.copy(timeFormat = TimeFormat.TWENTY_FOUR_HOURS),
            DEFAULT_SETTINGS.copy(timeFormat = TimeFormat.TWELVE_HOURS)
        )
        
        val FACTORY_RESET_SEQUENCE = listOf(
            PROFESSIONAL_SETTINGS,  // User's customized settings
            DEFAULT_SETTINGS        // After factory reset
        )
    }
}