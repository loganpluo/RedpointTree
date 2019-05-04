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

        val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree("messagebox")

        redpointTree!!.findRedPointById("system")!!.invalidate(0)

    }




}