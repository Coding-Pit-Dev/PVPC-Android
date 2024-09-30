package com.codingpit.pvpcplanner.data.mappers

import com.codingpit.pvpcplanner.domains.models.PVPCDTO
import com.codingpit.pvpcplanner.domains.models.PVPCModel

class DTOConvertToModel {
    fun map(dto: List<PVPCDTO>): List<PVPCModel> {
        return dto.mapNotNull { item ->
            if (item.dia != null && item.hora != null && item.pcb != null && item.cym != null) {
                PVPCModel(
                    dia = item.dia,
                    hora = item.hora,
                    pcb = item.pcb,
                    cym = item.cym
                )
            } else {
                null
            }
        }
    }
}
