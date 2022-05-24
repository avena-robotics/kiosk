package com.example.listviewlayout

import android.content.Intent
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.databinding.DataBindingUtil
import com.example.listviewlayout.databinding.ActivityStart2Binding
import com.example.listviewlayout.language.LocaleUtil


class StartActivity : BaseActivity() {

    private fun updateAppLocale(locale: String) {
        storage.setPreferredLocale(locale)
        LocaleUtil.applyLocalizedContext(applicationContext, locale)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityStart2Binding = DataBindingUtil.setContentView(this, R.layout.activity_start2)

        window.insetsController?.let {
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            window.navigationBarColor = getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
            it.hide(WindowInsets.Type.systemBars())
        }

        binding.button.setOnClickListener {
            val switchActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(switchActivityIntent)
        }

        binding.polishFlagButton.setOnClickListener {
            updateAppLocale("pl")
            recreate()
        }

        binding.englishFlagButton.setOnClickListener {
            updateAppLocale("en")
            recreate()
        }

    }
}