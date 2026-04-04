package com.codingpit.pvpcplanner.domain.usecase

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.codingpit.pvpcplanner.worker.PriceCheckWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val WORK_NAME = "price_alert_check"
private const val REPEAT_INTERVAL_HOURS = 1L

class SchedulePriceAlerts
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        operator fun invoke(thresholdPrice: Float) {
            val data = Data.Builder()
                .putFloat(PriceCheckWorker.KEY_THRESHOLD, thresholdPrice)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<PriceCheckWorker>(
                REPEAT_INTERVAL_HOURS,
                TimeUnit.HOURS,
            ).setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest,
            )
        }

        fun cancel() {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
