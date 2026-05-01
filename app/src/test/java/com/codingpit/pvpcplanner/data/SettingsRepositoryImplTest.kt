package com.codingpit.pvpcplanner.data

import app.cash.turbine.test
import com.codingpit.pvpcplanner.data.local.store.SettingsStore
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class SettingsRepositoryImplTest {
    private val mockStore = mockk<SettingsStore>()
    private lateinit var repository: SettingsRepositoryImpl

    @Before
    fun setup() {
        repository = SettingsRepositoryImpl(mockStore)
    }

    @Test
    fun `getSettings returns flow from store`() =
        runTest {
            val settings = Settings(DarkMode.DARK, TimeFormat.TWENTY_FOUR_HOURS, 5, 0.1f)
            every { mockStore.settings } returns flowOf(settings)

            repository.getSettings().test {
                assertEquals(settings, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `updateDarkMode delegates to store`() =
        runTest {
            coEvery { mockStore.updateDarkMode(DarkMode.DARK) } returns Unit

            repository.updateDarkMode(DarkMode.DARK)

            coVerify { mockStore.updateDarkMode(DarkMode.DARK) }
        }

    @Test
    fun `updateTimeFormat delegates to store`() =
        runTest {
            coEvery { mockStore.updateTimeFormat(TimeFormat.TWENTY_FOUR_HOURS) } returns Unit

            repository.updateTimeFormat(TimeFormat.TWENTY_FOUR_HOURS)

            coVerify { mockStore.updateTimeFormat(TimeFormat.TWENTY_FOUR_HOURS) }
        }

    @Test
    fun `updatePriceThreshold delegates to store for positive value`() =
        runTest {
            coEvery { mockStore.updatePriceThreshold(0.15f) } returns Unit

            repository.updatePriceThreshold(0.15f)

            coVerify { mockStore.updatePriceThreshold(0.15f) }
        }

    @Test
    fun `updatePriceThreshold allows zero value`() =
        runTest {
            coEvery { mockStore.updatePriceThreshold(0f) } returns Unit

            repository.updatePriceThreshold(0f)

            coVerify { mockStore.updatePriceThreshold(0f) }
        }

    @Test
    fun `updatePriceThreshold throws for negative value`() =
        runTest {
            try {
                repository.updatePriceThreshold(-0.1f)
                fail("Expected IllegalArgumentException")
            } catch (e: IllegalArgumentException) {
                assertEquals("priceThreshold must be >= 0", e.message)
            }
            coVerify(exactly = 0) { mockStore.updatePriceThreshold(any()) }
        }
}
