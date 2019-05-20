package com.github.redpointtree.demo.http

/**
 * Created by loganpluo on 2019/5/20.
 */
interface HttpRspCallBack<T> {

    fun onSuccess(response:T)

    fun onFail(code:Int, msg:String)

}