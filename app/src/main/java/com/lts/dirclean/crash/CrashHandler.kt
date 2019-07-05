package com.lts.dirclean.crash

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.lts.dirclean.BuildConfig
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {

    private lateinit var defaultUncaughtExceptionHandler : Thread.UncaughtExceptionHandler
    private lateinit var context: Context

    companion object {
        val instances by lazy { CrashHandler() }

    }

    fun init(context: Context) {

         defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler(this)

        this.context = context
    }

    override fun uncaughtException(t: Thread?, e: Throwable?) {

        dumpException(e)

        e?.printStackTrace()

        defaultUncaughtExceptionHandler.uncaughtException(t,e)
    }

    private fun dumpException(e: Throwable?) {

        val filesDir = context.getExternalFilesDir("log")
        val currentDate = getCurrentDate()
        val fileName = "crash_$currentDate _${System.currentTimeMillis()}.log"

        val file = File(filesDir,fileName)

        val pw = PrintWriter(BufferedWriter(FileWriter(file)))
        //crash时间
        pw.println(currentDate)

        //手机信息
        dumpPhoneInfo(pw)

        pw.println()

        e?.printStackTrace(pw)

        pw.close()
    }

    private fun dumpPhoneInfo(pw: PrintWriter) {
        //应用的版本名称和版本号
        pw.print("App Version: ")
        pw.print(BuildConfig.VERSION_NAME)
        pw.print('_')
        pw.println(BuildConfig.VERSION_CODE)

        //android版本号
        pw.print("OS Version: ")
        pw.print(Build.VERSION.RELEASE)
        pw.print("_")
        pw.println(Build.VERSION.SDK_INT)

        //手机制造商
        pw.print("Vendor: ")
        pw.println(Build.MANUFACTURER)

        //手机型号
        pw.print("Model: ")
        pw.println(Build.MODEL)

        //cpu架构
        pw.print("CPU ABI: ")
        pw.println(Build.SUPPORTED_ABIS)
    }

    private fun getCurrentDate(): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

        return format.format(Date(System.currentTimeMillis()))
    }
}