package com.example.profit

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.profit.reminder.ReminderWorker
import java.util.concurrent.TimeUnit

class ProfitApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                WorkManager.getInstance(this@ProfitApplication).cancelUniqueWork(WORK_NAME)
            }

            override fun onStop(owner: LifecycleOwner) {
                val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(5, TimeUnit.SECONDS)
                    .build()
                WorkManager.getInstance(this@ProfitApplication)
                    .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
            }
        })
    }

    companion object {
        const val WORK_NAME = "finance_reminder_work"
    }
}