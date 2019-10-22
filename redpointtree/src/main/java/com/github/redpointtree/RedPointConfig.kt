package com.github.redpointtree

/**
 * Created by loganpluo on 2019/5/3.
 */
object RedPointConfig {

    var redPointCachePreKey:IRedPointCachePreKey? = null
    var logConfig:LogConfig? = null

    interface IRedPointCachePreKey{
        fun getRedPointCachePreKey():String
    }

    interface LogConfig{
        fun d(tag:String, msg:String)

        fun i(tag:String, msg:String)

        fun w(tag:String, msg:String)

        fun e(tag:String, msg:String)
    }

}


