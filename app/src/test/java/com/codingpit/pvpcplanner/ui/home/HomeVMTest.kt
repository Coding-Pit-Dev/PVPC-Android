package com.codingpit.pvpcplanner.ui.home

import com.codingpit.pvpcplanner.data.Repository
import com.codingpit.pvpcplanner.mocks.Mocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeVMTest{

    // SUT
    private lateinit var viewModel: HomeVM
    private lateinit var mocks: Mocks

    // Dependencies
    private val repository: Repository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        mocks = Mocks()
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeVM(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given successful repository call When getPrices Then update state with Success`() = runTest {
        // Given
        coEvery { repository.getPrices() } returns mocks.mockPVPCModel

        // When
        viewModel.getPrices()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.dropWhile { it is HomeState.Loading }.first() // Ignoro el estado Loading
        Assertions.assertEquals(HomeState.Success(mocks.mockPVPCModel), state)
        coVerify { repository.getPrices() }
    }

    @Test
    fun `Given repository call failure When getPrices Then update state with Error`() = runTest {
        // Given
        val errorMessage = "Error fetching prices"
        coEvery { repository.getPrices() } throws Exception(errorMessage)

        // When
        viewModel.getPrices()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.dropWhile { it is HomeState.Loading }.first()
        Assertions.assertEquals(HomeState.Error(errorMessage), state)

        coVerify { repository.getPrices() }
    }
}
