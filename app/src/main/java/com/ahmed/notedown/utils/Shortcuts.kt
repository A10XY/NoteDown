package com.ahmed.notedown.utils

import android.content.Context
import android.os.Build
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun Context.shortToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/*fun simpleDateTime(): String{
    //                           Tuesday, 10 August 2021 02:09 PM
    return SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())
}*/

fun Context.simpleDateTime(): String{
    return when(Preferencer(this).getString("LANGUAGE")){
        "ar" -> {SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.forLanguageTag("ar")).format(Date())}
        else -> {SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.ENGLISH).format(Date())}
    }
}

fun voiceNoteFileName(): String{
    return SimpleDateFormat("EEEE-dd-MMMM-yyyy-HH:mm-a", Locale.getDefault()).format(Date())
}

fun apiLevelIsHigherOrEqualTo(apiNumber: Int): Boolean{
    return Build.VERSION.SDK_INT >= apiNumber
}