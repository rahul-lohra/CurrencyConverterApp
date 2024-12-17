package rahul.lohra.currencyconverter

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CurrencyConverterApp: Application() {
    companion object {
        lateinit var INSTANCE: CurrencyConverterApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}

fun Context.isAppInDebug(): Boolean {
    return (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}