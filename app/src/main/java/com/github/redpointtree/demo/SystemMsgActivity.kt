package com.github.redpointtree.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.redpointtree.demo.http.HttpRspCallBack
import com.github.redpointtree.demo.http.HttpUtils
import com.github.redpointtree.demo.http.test.SystemMsgListRequest
import com.github.redpointtree.demo.http.test.SystemMsgListRsp

class SystemMsgActivity : AppCompatActivity() {

    val tag = "SystemMsgActivity|RedpointTree"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_msg)

        //意思下
        HttpUtils.request("http://SystemMsgListRequest", SystemMsgListRequest(), object: HttpRspCallBack<SystemMsgListRsp> {
            override fun onSuccess(response: SystemMsgListRsp) {
                //do 刷新列表的
            }

            override fun onFail(code: Int, msg: String) {

            }

        })

    }




}