package com.codingpit.pvpcplanner.data.local.store

import kotlin.math.roundToInt

private const val PRICE_THRESHOLD_SCALE = 1000f

internal fun Float.toMilliEurosPerKwh(): Int =
    coerceAtLeast(0f)
        .times(PRICE_THRESHOLD_SCALE)
        .roundToInt()

internal fun Int.toPriceThresholdEurosPerKwh(): Float = this / PRICE_THRESHOLD_SCALE
