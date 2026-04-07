package com.codingpit.pvpcplanner.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.data.PriceRepository
import com.codingpit.pvpcplanner.data.local.store.toMilliEurosPerKwh
import com.codingpit.pvpcplanner.data.local.store.toPriceThresholdEurosPerKwh
import com.codingpit.pvpcplanner.utils.DateFormatter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PriceCheckWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val priceRepository: PriceRepository,
    ) : CoroutineWorker(context, params) {
        @Suppress("ReturnCount")
        override suspend fun doWork(): Result {
            val thresholdMilliEurosPerKwh = inputData.getInt(KEY_THRESHOLD_MILLI_EUROS_PER_KWH, 0)
            if (thresholdMilliEurosPerKwh <= 0) return Result.success()

            val currentDate = DateFormatter.formatDate(DateFormatter.getCurrentDate())
            val currentHour = DateFormatter.getCurrentHour()
            val priceFetchResult = priceRepository.getPrices(currentDate).getOrElse { return Result.retry() }
            val currentHourPrice = priceFetchResult.prices.firstOrNull { it.startHour == currentHour }?.pcb ?: return Result.success()
            val currentHourPriceMilliEurosPerKwh = currentHourPrice.toFloat().toMilliEurosPerKwh()

            if (currentHourPriceMilliEurosPerKwh > thresholdMilliEurosPerKwh) {
                return Result.success()
            }

            sendNotification(thresholdMilliEurosPerKwh)
            return Result.success()
        }

        private fun sendNotification(thresholdMilliEurosPerKwh: Int) {
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(
                        CHANNEL_ID,
                        applicationContext.getString(R.string.notification_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT,
                    )
                notificationManager.createNotificationChannel(channel)
            }

            val notification =
                NotificationCompat
                    .Builder(applicationContext, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(applicationContext.getString(R.string.notification_cheap_price_title))
                    .setContentText(
                        applicationContext.getString(
                            R.string.notification_cheap_price_body,
                            thresholdMilliEurosPerKwh.toPriceThresholdEurosPerKwh(),
                        ),
                    ).setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()

            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            notificationManager.notify(NOTIFICATION_ID, notification)
        }

        companion object {
            const val CHANNEL_ID = "pvpc_price_alerts"
            const val NOTIFICATION_ID = 1001
            const val KEY_THRESHOLD_MILLI_EUROS_PER_KWH = "threshold_milli_eur_per_kwh"
        }
    }
