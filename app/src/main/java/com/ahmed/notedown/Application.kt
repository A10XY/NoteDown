package com.ahmed.notedown

import android.app.Application
import android.util.Log
import com.ahmed.notedown.utils.Preferencer
import java.util.*

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        if (!hasUsedTheApp()){
            getSystemLanguage()
        }
        Preferencer(this).putBoolean("APP_USED", true)
    }
    private fun getSystemLanguage(){
        val currentLanguage = Locale.getDefault().language
        Preferencer(this).putString("LANGUAGE", currentLanguage)
    }

    private fun hasUsedTheApp(): Boolean{
        return Preferencer(this).getBoolean("APP_USED")
    }
}