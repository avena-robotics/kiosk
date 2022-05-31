package com.example.kiosk

import android.app.Application
import android.content.Context
import com.example.kiosk.language.LangStorage
import com.example.kiosk.language.LocaleUtil

class MyApp: Application() {
    val storage: LangStorage by lazy{
        LangStorage(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.getLocalizedContext(base, LangStorage(
            base).getPreferredLocale()))
    }
}