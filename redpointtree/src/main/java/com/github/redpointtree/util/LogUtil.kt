package com.github.redpointtree.util

import com.github.redpointtree.RedPointConfig

/**
 * Created by loganpluo on 2019/4/14.
 */
object LogUtil {

    fun d(tag:String, msg:String){
        RedPointConfig.logConfig?.d(tag, msg)
    }

    fun i(tag:String, msg:String){
        RedPointConfig.logConfig?.i(tag, msg)
    }

    fun w(tag:String, msg:String){
        RedPointConfig.logConfig?.w(tag, msg)
    }

    fun e(tag:String, msg:String){
        RedPointConfig.logConfig?.e(tag, msg)
    }


}