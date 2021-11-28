package com.ahmed.notedown.utils

import android.content.Context
import android.content.SharedPreferences

class Preferencer(context: Context) {
    private var sharedPreference: SharedPreferences = context.getSharedPreferences(
        KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun putBoolean(key: String, value: Boolean){
        sharedPreference.edit().apply{
            putBoolean(key, value)
        }.apply()
    }

    fun getBoolean(key: String): Boolean{
        return sharedPreference.getBoolean(key, false)
    }

    fun putString(key: String, value: String){
        sharedPreference.edit().apply{
            putString(key, value)
        }.apply()
    }

    fun getString(key: String): String{
        return sharedPreference.getString(key, null)!!
    }

    fun clear(){
        sharedPreference.edit().apply{
            clear()
        }.apply()
    }

    companion object{
        private const val KEY_PREFERENCE_NAME = "LANGUAGE"
    }
}