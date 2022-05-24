package com.example.listviewlayout

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.listviewlayout.language.LangStorage
import com.example.listviewlayout.language.LocaleUtil

open class BaseActivity: AppCompatActivity() {
    private lateinit var oldPrefLocaleCode: String
    protected val storage: LangStorage by lazy {
        (application as MyApp).storage
    }

    override fun attachBaseContext(newBase: Context) {
        oldPrefLocaleCode = LangStorage(newBase).getPreferredLocale()
        applyOverrideConfiguration(LocaleUtil.getLocalizedConfiguration(oldPrefLocaleCode))
        super.attachBaseContext(newBase)
    }

    override fun onResume() {
        val currentLocaleCode = LangStorage(this).getPreferredLocale()
        if (oldPrefLocaleCode != currentLocaleCode) {
            recreate() //locale is changed, restart the activty to update
            oldPrefLocaleCode = currentLocaleCode
        }
        super.onResume()
    }
}