package com.github.redpointtree.demo

import android.app.Application
import android.util.Log
import com.tencent.mmkv.MMKV

/**
 * Created by loganpluo on 2019/5/4.
 */
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val rootDir = MMKV.initialize(this)
        Log.d("MyApplication", "redpoint cache rootDir:" + rootDir)

    }

}