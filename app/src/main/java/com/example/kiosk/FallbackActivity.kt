package com.example.kiosk

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class FallbackActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fallback)

        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            println(packageManager.getLaunchIntentForPackage("com.example.kiosk"))
            val packages = packageManager.getInstalledPackages(0)
            for(i in packages){
                println(i.packageName)
            }
            val intent = Intent(this, MainActivity::class.java)
            if (intent != null){
                startActivity(intent)
            }else{
                println("ech")
            }
        }
    }
}