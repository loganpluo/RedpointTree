package com.github.redpointtree.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.redpointtree.demo.http.HttpRspCallBack
import com.github.redpointtree.demo.http.HttpUtils
import com.github.redpointtree.demo.http.test.MomentMsgListRequest
import com.github.redpointtree.demo.http.test.MomentMsgListRsp

class MomentMsgListActivity : AppCompatActivity() {

    val tag = "MomentMsgListActivity|RedpointTree"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moment_msg)

        //意思下
        HttpUtils.request("http://MomentMsgListRequest", MomentMsgListRequest(), object:HttpRspCallBack<MomentMsgListRsp>{
            override fun onSuccess(response: MomentMsgListRsp) {
                //do 刷新列表的
            }

            override fun onFail(code: Int, msg: String) {

            }

        })

    }

}