package com.codingpit.pvpcplanner.data.local.store

import kotlin.math.roundToInt

private const val PriceThresholdScale = 1000f

internal fun Float.toMilliEurosPerKwh(): Int =
    coerceAtLeast(0f)
        .times(PriceThresholdScale)
        .roundToInt()

internal fun Int.toPriceThresholdEurosPerKwh(): Float = this / PriceThresholdScale
