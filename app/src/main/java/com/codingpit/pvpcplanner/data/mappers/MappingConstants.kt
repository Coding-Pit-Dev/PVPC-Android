package com.codingpit.pvpcplanner.data.mappers

/**
 * Constants used in data mapping operations.
 */
object MappingConstants {

    /**
     * Price conversion factor for PVPC data.
     *
     * The API returns prices in a format where they need to be divided by 1000
     * to convert from the raw API format to the actual price units (€/MWh).
     * This is based on the PVPC API specification where values are provided
     * in a different scale than the final display format.
     */
    const val PRICE_CONVERSION_FACTOR = 1000.0

    /**
     * Separator used for entity ID generation.
     */
    const val ID_SEPARATOR = "_"

    /**
     * Decimal separator replacement character.
     * Used to normalize decimal separators from API responses.
     */
    const val DECIMAL_SEPARATOR_REPLACEMENT = "."

    /**
     * Original decimal separator from API.
     */
    const val ORIGINAL_DECIMAL_SEPARATOR = ","
}
