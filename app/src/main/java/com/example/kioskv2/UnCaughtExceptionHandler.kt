package com.example.kioskv2

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import kotlin.system.exitProcess


class UnCaughtExceptionHandler(var activity: Activity, val defaultUEH: Thread.UncaughtExceptionHandler) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(p0: Thread, p1: Throwable) {
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)

        //val pendingIntent = PendingIntent.getActivity(activity.baseContext, 192837, intent, PendingIntent.FLAG_ONE_SHOT)

        //val alarmManager: AlarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)

        System.exit(10)

    }
}