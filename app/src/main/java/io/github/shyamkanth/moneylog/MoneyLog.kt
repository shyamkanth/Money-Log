package io.github.shyamkanth.moneylog

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.shyamkanth.moneylog.utils.NotificationHelper

@HiltAndroidApp
class MoneyLog : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.init(applicationContext)
    }
}