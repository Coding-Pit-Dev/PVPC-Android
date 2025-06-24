package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.Repository
import com.codingpit.pvpcplanner.domain.models.Device
import javax.inject.Inject

class UpdateDevice
    @Inject
    constructor(private val repository: Repository) {
        suspend operator fun invoke(device: Device) = repository.updateDevice(device)
    }
