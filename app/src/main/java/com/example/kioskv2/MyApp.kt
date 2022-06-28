package com.example.kioskv2

import android.app.Application
import android.content.Context
import com.example.kioskv2.language.LangStorage
import com.example.kioskv2.language.LocaleUtil

class MyApp: Application() {

    val storage: LangStorage by lazy{
        LangStorage(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.getLocalizedContext(base, LangStorage(base).getPreferredLocale()))
    }
}