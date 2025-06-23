package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.Repository
import com.codingpit.pvpcplanner.domain.models.Device
import javax.inject.Inject

class DeleteDevice
    @Inject
    constructor(private val repository: Repository) {
        suspend operator fun invoke(device: Device) = repository.deleteDevice(device)
    }
