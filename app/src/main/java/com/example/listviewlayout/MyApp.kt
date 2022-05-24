package com.example.listviewlayout

import android.app.Application
import android.content.Context
import com.example.listviewlayout.language.LangStorage
import com.example.listviewlayout.language.LocaleUtil

class MyApp: Application() {
    val storage: LangStorage by lazy{
        LangStorage(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.getLocalizedContext(base, LangStorage(
            base).getPreferredLocale()))
    }
}