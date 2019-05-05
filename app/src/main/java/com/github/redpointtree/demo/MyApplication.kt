package com.github.redpointtree.demo

import android.app.Application
import android.util.Log
import com.github.redpointtree.RedPointConfig
import com.tencent.mmkv.MMKV

/**
 * Created by loganpluo on 2019/5/4.
 */
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val rootDir = MMKV.initialize(this)
        Log.d("MyApplication", "redpoint cache rootDir:" + rootDir)

        RedPointConfig.logConfig = object:RedPointConfig.LogConfig{
            override fun d(tag: String, msg: String) {
                Log.d(tag, msg)
            }

            override fun i(tag: String, msg: String) {
                Log.i(tag, msg)
            }

            override fun w(tag: String, msg: String) {
                Log.w(tag, msg)
            }

            override fun e(tag: String, msg: String) {
                Log.e(tag, msg)
            }

        }

        RedPointConfig.redPointCachePreKey = object:RedPointConfig.IRedPointCachePreKey{
            override fun getRedPointCachePreKey(): String {
                return "1"
            }

        }


    }

}