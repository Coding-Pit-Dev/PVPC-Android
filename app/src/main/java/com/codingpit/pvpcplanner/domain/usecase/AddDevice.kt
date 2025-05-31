package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.Repository
import com.codingpit.pvpcplanner.domain.models.Device
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddDevice @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(device: Device) =
        repository.addDevice(device)

}