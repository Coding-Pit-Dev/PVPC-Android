package com.codingpit.pvpcplanner.fixtures

import com.codingpit.pvpcplanner.domain.models.PVPCModel

/**
 * Test fixtures for PVPC (electricity price) data.
 * Provides realistic sample data for testing various scenarios.
 */
object PVPCTestFixtures {

    /*
    /**
     * Sample PVPC response with typical daily price pattern.
     * Prices are lower during night hours and higher during day hours.
     */
    val TYPICAL_DAILY_RESPONSE = PVPCResponse(
        included = listOf(
            PVPCDTO(
                type = "PVPC",
                id = "1001",
                attributes = mapOf(
                    "title" to "PVPC (€/MWh) 15-10-2023",
                    "last-update" to "2023-10-14T20:15:12.000+02:00",
                    "description" to "Precio Voluntario para el Pequeño Consumidor"
                ),
                hourly = PVPCDTO.Hourly(
                    value1 = 120.50,  // 00-01h - Night (cheap)
                    value2 = 115.30,  // 01-02h - Night (cheap)
                    value3 = 110.20,  // 02-03h - Night (cheapest)
                    value4 = 112.40,  // 03-04h - Night
                    value5 = 118.60,  // 04-05h - Early morning
                    value6 = 125.80,  // 05-06h - Early morning
                    value7 = 145.90,  // 06-07h - Morning rush (expensive)
                    value8 = 165.70,  // 07-08h - Morning rush (very expensive)
                    value9 = 155.30,  // 08-09h - Morning
                    value10 = 140.20, // 09-10h - Morning
                    value11 = 135.60, // 10-11h - Mid-morning
                    value12 = 130.40, // 11-12h - Noon
                    value13 = 128.80, // 12-13h - Lunch
                    value14 = 132.50, // 13-14h - Afternoon
                    value15 = 138.70, // 14-15h - Afternoon
                    value16 = 142.30, // 15-16h - Afternoon
                    value17 = 148.90, // 16-17h - Evening
                    value18 = 158.60, // 17-18h - Evening rush
                    value19 = 170.40, // 18-19h - Peak evening (most expensive)
                    value20 = 162.80, // 19-20h - Evening
                    value21 = 152.30, // 20-21h - Evening
                    value22 = 141.70, // 21-22h - Late evening
                    value23 = 135.20, // 22-23h - Night
                    value24 = 128.40  // 23-24h - Night
                )
            )
        )
    )
    
    /**
     * Response with uniformly high prices (expensive day scenario).
     */
    val HIGH_PRICE_RESPONSE = PVPCResponse(
        included = listOf(
            PVPCDTO(
                type = "PVPC",
                id = "1002",
                attributes = mapOf(
                    "title" to "PVPC (€/MWh) 16-10-2023 - High Price Day",
                    "last-update" to "2023-10-15T20:15:12.000+02:00",
                    "description" to "High demand day"
                ),
                hourly = PVPCDTO.Hourly(
                    value1 = 180.50, value2 = 185.30, value3 = 175.20, value4 = 178.40,
                    value5 = 188.60, value6 = 195.80, value7 = 205.90, value8 = 215.70,
                    value9 = 210.30, value10 = 202.20, value11 = 198.60, value12 = 195.40,
                    value13 = 192.80, value14 = 196.50, value15 = 201.70, value16 = 208.30,
                    value17 = 218.90, value18 = 225.60, value19 = 235.40, value20 = 230.80,
                    value21 = 220.30, value22 = 205.70, value23 = 195.20, value24 = 188.40
                )
            )
        )
    )
    
    /**
     * Response with uniformly low prices (renewable energy abundant scenario).
     */
    val LOW_PRICE_RESPONSE = PVPCResponse(
        included = listOf(
            PVPCDTO(
                type = "PVPC",
                id = "1003",
                attributes = mapOf(
                    "title" to "PVPC (€/MWh) 17-10-2023 - Low Price Day",
                    "last-update" to "2023-10-16T20:15:12.000+02:00",
                    "description" to "High renewable energy production"
                ),
                hourly = PVPCDTO.Hourly(
                    value1 = 45.50, value2 = 42.30, value3 = 38.20, value4 = 40.40,
                    value5 = 44.60, value6 = 48.80, value7 = 55.90, value8 = 62.70,
                    value9 = 58.30, value10 = 52.20, value11 = 49.60, value12 = 47.40,
                    value13 = 46.80, value14 = 48.50, value15 = 52.70, value16 = 57.30,
                    value17 = 63.90, value18 = 68.60, value19 = 72.40, value20 = 69.80,
                    value21 = 64.30, value22 = 58.70, value23 = 52.20, value24 = 48.40
                )
            )
        )
    )
    
    /**
     * Response with minimal variation (flat pricing scenario).
     */
    val FLAT_PRICE_RESPONSE = PVPCResponse(
        included = listOf(
            PVPCDTO(
                type = "PVPC",
                id = "1004",
                attributes = mapOf(
                    "title" to "PVPC (€/MWh) 18-10-2023 - Flat Pricing",
                    "last-update" to "2023-10-17T20:15:12.000+02:00",
                    "description" to "Stable demand and supply"
                ),
                hourly = PVPCDTO.Hourly(
                    value1 = 125.00, value2 = 125.50, value3 = 124.80, value4 = 125.20,
                    value5 = 124.90, value6 = 125.30, value7 = 125.10, value8 = 124.70,
                    value9 = 125.40, value10 = 124.60, value11 = 125.80, value12 = 124.40,
                    value13 = 125.60, value14 = 124.90, value15 = 125.70, value16 = 124.30,
                    value17 = 125.90, value18 = 124.10, value19 = 126.00, value20 = 124.00,
                    value21 = 125.20, value22 = 124.80, value23 = 125.10, value24 = 124.90
                )
            )
        )
    )

     */

    /*/**
     * Empty response for error scenarios.
     */
    val EMPTY_RESPONSE = PVPCResponse(included = emptyList())


     */
    /**
     * Domain model fixtures derived from the DTO responses.
     */
    object Models {
        val TYPICAL_DAILY_PRICES = listOf(
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
            PVPCModel("2023-10-15", 23, 24, 0.12840, 0.15505)
        )

        val HIGH_PRICES = listOf(
            PVPCModel("2023-10-16", 0, 1, 0.18050, 0.21780),
            PVPCModel("2023-10-16", 1, 2, 0.18530, 0.22360),
            PVPCModel("2023-10-16", 2, 3, 0.17520, 0.21138),
            PVPCModel("2023-10-16", 3, 4, 0.17840, 0.21525)
            // ... truncated for brevity, would include all 24 hours
        )

        val LOW_PRICES = listOf(
            PVPCModel("2023-10-17", 0, 1, 0.04550, 0.05494),
            PVPCModel("2023-10-17", 1, 2, 0.04230, 0.05108),
            PVPCModel("2023-10-17", 2, 3, 0.03820, 0.04613), // Very cheap
            PVPCModel("2023-10-17", 3, 4, 0.04040, 0.04878)
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
