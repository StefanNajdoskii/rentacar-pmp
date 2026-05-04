package com.rentacar.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReturnReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val carName = inputData.getString(KEY_CAR_NAME) ?: return Result.failure()
        val returnDate = inputData.getString(KEY_RETURN_DATE) ?: return Result.failure()
        NotificationHelper.showReturnReminder(applicationContext, carName, returnDate)
        return Result.success()
    }

    companion object {
        const val KEY_CAR_NAME = "car_name"
        const val KEY_RETURN_DATE = "return_date"
    }
}
