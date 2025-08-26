package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.DeviceRepository
import com.codingpit.pvpcplanner.domain.models.Device
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDevices
@Inject
constructor(private val repository: DeviceRepository) {
    operator fun invoke(): Flow<List<Device>> = repository.getDevices()
}
