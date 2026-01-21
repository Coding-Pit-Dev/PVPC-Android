package com.codingpit.pvpcplanner.data.mappers

import com.codingpit.pvpcplanner.data.remote.response.PVPCDTO
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import org.junit.Assert.assertEquals
import org.junit.Test

class PvpcDtoMapperTest {

    @Test
    fun `PVPCDTO toDomain maps correctly with price conversion`() {
        // Arrange
        val dto = PVPCDTO(
            day = "2023-10-15",
            hour = "10-11",
            pcb = "150,25", // Using comma as decimal separator
            cym = "125,50",
            cof2td = null,
            pmhpcb = null,
            pmhcyM = null,
            sahpcb = null,
            sahcym = null,
            fompcb = null,
            fomcym = null,
            fospcb = null,
            foscyM = null,
            intpcb = null,
            intcym = null,
            cappcb = null,
            capcym = null,
            teupcb = null,
            teucym = null,
            ccvpcb = null,
            ccvcyM = null,
            edsrpcb = null,
            edsrcym = null,
            tahpcb = null,
            tahcym = null
        )

        // Act
        val result = dto.toDomain()

        // Assert
        assertEquals("2023-10-15", result.day)
        assertEquals(10, result.startHour)
        assertEquals(11, result.endHour)
        assertEquals(0.15025, result.pcb, 0.00001) // 150.25 / 1000
        assertEquals(0.12550, result.cym, 0.00001) // 125.50 / 1000
    }

    @Test
    fun `PVPCModel toEntity generates correct ID format`() {
        // Arrange
        val model = PVPCModel(
            day = "2023-10-15",
            startHour = 10,
            endHour = 11,
            pcb = 0.15025,
            cym = 0.12550
        )

        // Act
        val result = model.toEntity()

        // Assert
        assertEquals("2023-10-15_10", result.id)
        assertEquals("2023-10-15", result.day)
        assertEquals(10, result.startHour)
        assertEquals(11, result.endHour)
        assertEquals(0.15025, result.pcb, 0.00001)
        assertEquals(0.12550, result.cym, 0.00001)
    }

    @Test
    fun `List PVPCDTO toDomain maps all items correctly`() {
        // Arrange
        val dtoList = listOf(
            PVPCDTO(
                day = "2023-10-15",
                hour = "10-11",
                pcb = "150,25",
                cym = "125,50",
                cof2td = null,
                pmhpcb = null,
                pmhcyM = null,
                sahpcb = null,
                sahcym = null,
                fompcb = null,
                fomcym = null,
                fospcb = null,
                foscyM = null,
                intpcb = null,
                intcym = null,
                cappcb = null,
                capcym = null,
                teupcb = null,
                teucym = null,
                ccvpcb = null,
                ccvcyM = null,
                edsrpcb = null,
                edsrcym = null,
                tahpcb = null,
                tahcym = null
            ),
            PVPCDTO(
                day = "2023-10-15",
                hour = "11-12",
                pcb = "200,75",
                cym = "175,80",
                cof2td = null,
                pmhpcb = null,
                pmhcyM = null,
                sahpcb = null,
                sahcym = null,
                fompcb = null,
                fomcym = null,
                fospcb = null,
                foscyM = null,
                intpcb = null,
                intcym = null,
                cappcb = null,
                capcym = null,
                teupcb = null,
                teucym = null,
                ccvpcb = null,
                ccvcyM = null,
                edsrpcb = null,
                edsrcym = null,
                tahpcb = null,
                tahcym = null
            )
        )

        // Act
        val result = dtoList.toDomain()

        // Assert
        assertEquals(2, result.size)
        assertEquals(10, result[0].startHour)
        assertEquals(11, result[1].startHour)
        assertEquals(0.15025, result[0].pcb, 0.00001)
        assertEquals(0.20075, result[1].pcb, 0.00001)
    }

    @Test
    fun `price conversion uses correct constants`() {
        // Arrange
        val dto = PVPCDTO(
            day = "2023-10-15",
            hour = "10-11",
            pcb = "1000,00",
            cym = "2000,00",
            cof2td = null,
            pmhpcb = null,
            pmhcyM = null,
            sahpcb = null,
            sahcym = null,
            fompcb = null,
            fomcym = null,
            fospcb = null,
            foscyM = null,
            intpcb = null,
            intcym = null,
            cappcb = null,
            capcym = null,
            teupcb = null,
            teucym = null,
            ccvpcb = null,
            ccvcyM = null,
            edsrpcb = null,
            edsrcym = null,
            tahpcb = null,
            tahcym = null
        )

        // Act
        val result = dto.toDomain()

        // Assert
        // Verify that PRICE_CONVERSION_FACTOR (1000) is used correctly
        assertEquals(1.0, result.pcb, 0.00001)
        assertEquals(2.0, result.cym, 0.00001)
    }
}
