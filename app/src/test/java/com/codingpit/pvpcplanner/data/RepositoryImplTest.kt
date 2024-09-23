package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.data.mappers.DTOConvertToModel
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.mocks.Mocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RepositoryImplTest {

    //SUT
    private lateinit var repository: RepositoryImpl
    private lateinit var mocks: Mocks

    //Dependencies
    private val remoteDataSource: RemoteDataSource = mockk()
    private val dtoConvertToModel = DTOConvertToModel()


    @BeforeEach
    fun setUp() {
        mocks = Mocks()
        repository = RepositoryImpl(remoteDataSource)
    }

    @Test
    fun `Given successful response When getPrices Then convert class and return a list of PVPCModel`() = runBlocking {
        // Given
        coEvery { remoteDataSource.getPrices() } returns mocks.mockPVPCDTO

        // When
        val expectedModel = dtoConvertToModel.map(mocks.mockPVPCDTO) // Convierto DTO a Model
        val result = repository.getPrices()

        // Then
        Assertions.assertEquals(expectedModel, result)
        coVerify { remoteDataSource.getPrices() }
    }
}