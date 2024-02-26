package com.finogeeks.finclip.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.ProcessUtils

abstract class BaseApplication : Application() {

    companion object {
        lateinit var context: Application
            private set
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
    override fun onCreate() {
        onPreCreate()
        super.onCreate()
        doInMain()
        if(ProcessUtils.isMainProcess()){
            doInMainProcess()
        }
        Thread {
            doInBackground()
        }.start()
    }
    open fun onPreCreate(){
        context = this
    }
    abstract  fun doInMain()
    abstract  fun doInMainProcess()
    open  fun doInBackground(){}
}