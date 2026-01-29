package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.SettingsRepository
import javax.inject.Inject

class GetSettings
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) {
        operator fun invoke() = settingsRepository.getSettings()
    }
