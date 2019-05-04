package com.github.redpointtree

/**
 * Created by loganpluo on 2019/5/3.
 */
object RedPointCacheConfig {

    var redPointCachePreKey:IRedPointCachePreKey? = null

    interface IRedPointCachePreKey{
        fun getRedPointCachePreKey():String
    }

}


