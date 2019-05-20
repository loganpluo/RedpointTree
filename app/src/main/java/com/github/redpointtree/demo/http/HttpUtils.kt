package com.github.redpointtree.demo.http

import com.github.redpointtree.demo.http.test.MessageBoxUnReadCountRsp
import com.google.gson.Gson

/**
 * Created by loganpluo on 2019/5/20.
 */
object HttpUtils {

    var requestFinishListener:RequestFinishListener? = null

    inline
    fun <reified T> request(param:Any, callback:HttpRspCallBack<T>){

        //示意下
        //do 异步请求
        //请求成功率
        //gson
        //{""}
        val testData = "{}"
        var rsp:T?
        if(T::class.java == MessageBoxUnReadCountRsp::class.java){
            rsp = MessageBoxUnReadCountRsp(0,12,1) as T
        }else{
            rsp = Gson().fromJson<T>(testData,T::class.java)
        }

        callback.onSuccess(rsp!!)
        requestFinishListener?.onSuccess(param, rsp as Any)

    }


}