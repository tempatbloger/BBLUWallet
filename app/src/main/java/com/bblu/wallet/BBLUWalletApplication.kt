package com.bblu.wallet

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class BBLUWalletApplication : Application() {
    
    companion object {
        lateinit var instance: BBLUWalletApplication
            private set
        
        fun getAppContext(): Context = instance.applicationContext
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Set default night mode ke light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        // Initialize crash handler
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleUncaughtException(thread, throwable)
        }
    }
    
    private fun handleUncaughtException(thread: Thread, throwable: Throwable) {
        throwable.printStackTrace()
        // Log error ke file atau service crash reporting
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(1)
    }
}
