package com.github.redpointtree.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.redpointtree.demo.http.HttpRspCallBack
import com.github.redpointtree.demo.http.HttpUtils
import com.github.redpointtree.demo.http.test.MessageBoxUnReadCountRequest
import com.github.redpointtree.demo.http.test.MessageBoxUnReadCountRsp
import kotlinx.android.synthetic.main.activity_cross_hierarchy.*

class CrossHierarchyActivity : AppCompatActivity() {

    val tag = "CrossHierarchyActivity|RedpointTree"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cross_hierarchy)

        rootView.setOnClickListener {
            val intent = Intent(this@CrossHierarchyActivity,MessageBoxActivity::class.java)
            startActivity(intent)
        }

        HttpUtils.request(MessageBoxUnReadCountRequest(), object:HttpRspCallBack<MessageBoxUnReadCountRsp>{

            override fun onFail(code: Int, msg: String) {

            }

            override fun onSuccess(response: MessageBoxUnReadCountRsp) {
                //MessageBoxUnReadCountRsp注解绑定了节点 会自动更新，不用代码更新了
            }

        })

    }


}