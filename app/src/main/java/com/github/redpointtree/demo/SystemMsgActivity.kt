package com.github.redpointtree.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.redpointtree.RedPointTreeCenter

class SystemMsgActivity : AppCompatActivity() {

    val tag = "SystemMsgActivity|RedpointTree"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_msg)

        readSystemMsgs()
    }

    private fun readSystemMsgs(){

        val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree(getString(R.string.messagebox_tree))

        redpointTree!!.findRedPointById(R.string.messagebox_system)!!.invalidate(0)
        //通常还需要拉去消息列表第一页成功后，invalidate(0) (防止用户停留在这个页面，下拉刷新)

    }




}