package com.github.redpointtree

import android.content.Context
import android.support.annotation.XmlRes
//import java.util.concurrent.ConcurrentHashMap

/**
 * Created by loganpluo on 2019/4/22.
 */
class RedPointTreeCenter private constructor() {

    private object  RedPointTreeInstancesHolder{
        val HOLDER = RedPointTreeCenter()
    }

    companion object {
        fun getInstance(): RedPointTreeCenter {
            return RedPointTreeInstancesHolder.HOLDER
        }
    }

    val redPointTreeMap = HashMap<String,RedpointTree>()//Concurrent

    fun getRedPointTree(redpointTreeName: String):RedpointTree?{
        return redPointTreeMap[redpointTreeName]
    }

    fun put(context: Context, redpointTreeName: String, @XmlRes xml:Int){
        redPointTreeMap.put(redpointTreeName, RedpointTree(context, xml))
    }

    fun remove(redpointTreeName: String){
        redPointTreeMap.remove(redpointTreeName)
    }



}