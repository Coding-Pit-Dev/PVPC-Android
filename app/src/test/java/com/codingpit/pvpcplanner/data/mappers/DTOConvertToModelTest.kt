package com.codingpit.pvpcplanner.data.mappers

import com.codingpit.pvpcplanner.mocks.Mocks
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach


class DTOConvertToModelTest {

    // SUT
    private lateinit var dtoConvertToModel: DTOConvertToModel
    private lateinit var mocks: Mocks

    @BeforeEach
    fun setUp() {
        mocks = Mocks()
        dtoConvertToModel = DTOConvertToModel()
    }

    @org.junit.jupiter.api.Test
    fun `Given a list of PVPCDTO When map is called Then return a list of PVPCModel`() {
        // Given
        val dtoList = mocks.mockPVPCDTO

        // When
        val result = dtoConvertToModel.map(dtoList)

        // Then
        Assertions.assertEquals(mocks.mockPVPCModel, result)
    }
}
