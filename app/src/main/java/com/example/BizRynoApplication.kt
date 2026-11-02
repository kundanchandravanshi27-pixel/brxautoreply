package com.example

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.example.worker.HealthCheckWorker
import java.util.concurrent.TimeUnit

class BizRynoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        
        // Schedule periodic health check worker (15 min)
        val healthRequest = PeriodicWorkRequestBuilder<HealthCheckWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "BizRynoHealthCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            healthRequest
        )
    }
}
