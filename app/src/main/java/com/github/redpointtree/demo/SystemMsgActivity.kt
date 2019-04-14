package com.github.redpointtree.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class SystemMsgActivity : AppCompatActivity() {

    val tag = "SystemMsgActivity|RepointTree"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_msg)

        readSystemMsgs()
    }

    private fun readSystemMsgs(){

        val repointTree = MessageBoxManager.getInstance(this).repointTree

        repointTree.findRedPointById("system")!!.invalidate(0)

    }




}