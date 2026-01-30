package com.codingpit.pvpcplanner.fixtures

import com.codingpit.pvpcplanner.domain.models.PVPCModel

/**
 * Test fixtures for PVPC (electricity price) data.
 * Provides realistic sample data for testing various scenarios.
 */
object PVPCTestFixtures {
    /**
     * Domain model fixtures derived from the DTO responses.
     */
    object Models {
        val TYPICAL_DAILY_PRICES =
            listOf(
                PVPCModel("2023-10-15", 0, 1, 0.12050, 0.14540),
                PVPCModel("2023-10-15", 1, 2, 0.11530, 0.13921),
                PVPCModel("2023-10-15", 2, 3, 0.11020, 0.13305), // Cheapest
                PVPCModel("2023-10-15", 3, 4, 0.11240, 0.13572),
                PVPCModel("2023-10-15", 4, 5, 0.11860, 0.14320),
                PVPCModel("2023-10-15", 5, 6, 0.12580, 0.15190),
                PVPCModel("2023-10-15", 6, 7, 0.14590, 0.17612),
                PVPCModel("2023-10-15", 7, 8, 0.16570, 0.20001),
                PVPCModel("2023-10-15", 8, 9, 0.15530, 0.18745),
                PVPCModel("2023-10-15", 9, 10, 0.14020, 0.16924),
                PVPCModel("2023-10-15", 10, 11, 0.13560, 0.16369),
                PVPCModel("2023-10-15", 11, 12, 0.13040, 0.15741),
                PVPCModel("2023-10-15", 12, 13, 0.12880, 0.15548),
                PVPCModel("2023-10-15", 13, 14, 0.13250, 0.15995),
                PVPCModel("2023-10-15", 14, 15, 0.13870, 0.16744),
                PVPCModel("2023-10-15", 15, 16, 0.14230, 0.17179),
                PVPCModel("2023-10-15", 16, 17, 0.14890, 0.17976),
                PVPCModel("2023-10-15", 17, 18, 0.15860, 0.19148),
                PVPCModel("2023-10-15", 18, 19, 0.17040, 0.20573), // Most expensive
                PVPCModel("2023-10-15", 19, 20, 0.16280, 0.19654),
                PVPCModel("2023-10-15", 20, 21, 0.15230, 0.18387),
                PVPCModel("2023-10-15", 21, 22, 0.14170, 0.17105),
                PVPCModel("2023-10-15", 22, 23, 0.13520, 0.16321),
                PVPCModel("2023-10-15", 23, 24, 0.12840, 0.15505),
            )

        val HIGH_PRICES =
            listOf(
                PVPCModel("2023-10-16", 0, 1, 0.18050, 0.21780),
                PVPCModel("2023-10-16", 1, 2, 0.18530, 0.22360),
                PVPCModel("2023-10-16", 2, 3, 0.17520, 0.21138),
                PVPCModel("2023-10-16", 3, 4, 0.17840, 0.21525),
                // ... truncated for brevity, would include all 24 hours
            )

        val LOW_PRICES =
            listOf(
                PVPCModel("2023-10-17", 0, 1, 0.04550, 0.05494),
                PVPCModel("2023-10-17", 1, 2, 0.04230, 0.05108),
                PVPCModel("2023-10-17", 2, 3, 0.03820, 0.04613), // Very cheap
                PVPCModel("2023-10-17", 3, 4, 0.04040, 0.04878),
                // ... truncated for brevity
            )
    }

    /**
     * Network error responses for testing error handling.
     */
    object Errors {
        const val NETWORK_ERROR_MESSAGE = "Network connection failed"
        const val API_ERROR_MESSAGE = "API service unavailable"
        const val TIMEOUT_ERROR_MESSAGE = "Request timeout"
        const val PARSE_ERROR_MESSAGE = "Failed to parse response"

        val networkException = RuntimeException(NETWORK_ERROR_MESSAGE)
        val apiException = RuntimeException(API_ERROR_MESSAGE)
        val timeoutException = RuntimeException(TIMEOUT_ERROR_MESSAGE)
        val parseException = RuntimeException(PARSE_ERROR_MESSAGE)
    }

    /**
     * Common test dates for consistent testing.
     */
    object TestDates {
        const val TYPICAL_DATE = "2023-10-15"
        const val HIGH_PRICE_DATE = "2023-10-16"
        const val LOW_PRICE_DATE = "2023-10-17"
        const val FLAT_PRICE_DATE = "2023-10-18"
        const val WEEKEND_DATE = "2023-10-14" // Saturday
        const val HOLIDAY_DATE = "2023-12-25" // Christmas
        const val INVALID_DATE = "2023-13-45"
        const val FUTURE_DATE = "2025-01-01"
        const val PAST_DATE = "2020-01-01"
    }
}
