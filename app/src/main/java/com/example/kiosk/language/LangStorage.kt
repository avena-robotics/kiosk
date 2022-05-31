package com.example.kiosk.language

import android.content.Context
import android.content.SharedPreferences

//Based on https://medium.com/swlh/the-all-in-one-guide-for-changing-app-locale-dynamically-in-android-kotlin-d2506e5535d0

class LangStorage(context: Context){
    private var preferences: SharedPreferences = context.getSharedPreferences("pl", Context.MODE_PRIVATE)


    fun getPreferredLocale(): String {
        return preferences.getString("preferred_locale", LocaleUtil.OPTION_PHONE_LANGUAGE)!!
    }

    fun setPreferredLocale(localeCode: String) {
        preferences.edit().putString("preferred_locale", localeCode).apply()
    }
}