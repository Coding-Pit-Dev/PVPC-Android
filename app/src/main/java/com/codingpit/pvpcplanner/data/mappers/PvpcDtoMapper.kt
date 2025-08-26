package com.codingpit.pvpcplanner.data.mappers

import com.codingpit.pvpcplanner.data.local.db.model.DeviceEntity
import com.codingpit.pvpcplanner.data.local.db.model.PVPCEntity
import com.codingpit.pvpcplanner.data.mappers.MappingConstants.DECIMAL_SEPARATOR_REPLACEMENT
import com.codingpit.pvpcplanner.data.mappers.MappingConstants.ID_SEPARATOR
import com.codingpit.pvpcplanner.data.mappers.MappingConstants.ORIGINAL_DECIMAL_SEPARATOR
import com.codingpit.pvpcplanner.data.mappers.MappingConstants.PRICE_CONVERSION_FACTOR
import com.codingpit.pvpcplanner.data.remote.response.PVPCDTO
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel

fun List<PVPCDTO>.toDomain(): List<PVPCModel> =
    this.map {
        it.toDomain()
    }

fun PVPCDTO.toDomain(): PVPCModel =
    (this.hour.split("-")).let {
        PVPCModel(
            day = this.day,
            startHour = it.first().toInt(),
            endHour = it[1].toInt(),
            pcb = this.pcb.replace(ORIGINAL_DECIMAL_SEPARATOR, DECIMAL_SEPARATOR_REPLACEMENT)
                .toDouble() / PRICE_CONVERSION_FACTOR,
            cym = this.cym.replace(ORIGINAL_DECIMAL_SEPARATOR, DECIMAL_SEPARATOR_REPLACEMENT)
                .toDouble() / PRICE_CONVERSION_FACTOR,
        )
    }

fun PVPCEntity.toDomain(): PVPCModel =
    PVPCModel(
        day = this.day,
        startHour = this.startHour,
        endHour = this.endHour,
        pcb = this.pcb,
        cym = this.cym,
    )

fun PVPCModel.toEntity(): PVPCEntity =
    PVPCEntity(
        id = "${day}${ID_SEPARATOR}${startHour}",
        day = this.day,
        startHour = this.startHour,
        endHour = this.endHour,
        pcb = this.pcb,
        cym = this.cym,
    )

fun DeviceEntity.toDomain(): Device =
    Device(
        id = id,
        name = name,
        hours = hours,
        icon = icon,
    )

fun Device.toEntity(): DeviceEntity =
    DeviceEntity(
        id = id,
        name = name,
        hours = hours,
        icon = icon,
    )
