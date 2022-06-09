package com.example.kiosk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.navigation.fragment.NavHostFragment
import com.example.kiosk.language.LocaleUtil

class MainActivity : BaseActivity() {

    fun updateAppLocale(locale: String) {
        storage.setPreferredLocale(locale)
        LocaleUtil.applyLocalizedContext(applicationContext, locale)
        recreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        Thread.setDefaultUncaughtExceptionHandler(UnCaughtExceptionHandler(this))

        window.insetsController?.let {
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            window.navigationBarColor = getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
            it.hide(WindowInsets.Type.systemBars())
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }
}