package com.example.kiosk

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlin.system.exitProcess


class UnCaughtExceptionHandler(var activity: Activity, val defaultUEH: Thread.UncaughtExceptionHandler) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(p0: Thread, p1: Throwable) {
        /*
        println("auć its down")
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_CLEAR_TASK
                or Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(activity, 123456, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr: AlarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent)
        //exitProcess(0)
        android.os.Process.killProcess(android.os.Process.myPid())

         */
        val intent = Intent(activity, FallbackActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(activity.baseContext, 192837, intent, PendingIntent.FLAG_ONE_SHOT)

        val alarmManager: AlarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 15000, pendingIntent)
        //System.exit(2)

        defaultUEH.uncaughtException(p0, p1)
    }
}