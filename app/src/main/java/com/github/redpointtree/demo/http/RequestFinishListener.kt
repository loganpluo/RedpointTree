package com.github.redpointtree.demo.http

/**
 * Created by loganpluo on 2019/5/20.
 */
interface RequestFinishListener {

    fun onSuccess(url:String, param:Any, response:Any)

}