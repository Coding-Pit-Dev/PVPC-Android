package com.codingpit.pvpcplanner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.usecase.GetSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ThemeManager
@Inject
constructor(
    private val getSettings: GetSettings,
) {
    fun getSettings(): Flow<Settings> = getSettings.invoke()

    @Composable
    fun shouldShowDarkTheme(darkMode: DarkMode): Boolean =
        when (darkMode) {
            DarkMode.LIGHT -> false
            DarkMode.DARK -> true
            DarkMode.SYSTEM -> isSystemInDarkTheme()
        }
}
