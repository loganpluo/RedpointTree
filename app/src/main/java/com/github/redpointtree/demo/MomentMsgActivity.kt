package com.github.redpointtree.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MomentMsgActivity : AppCompatActivity() {

    val tag = "MomentMsgActivity|RedpointTree"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moment_msg)

        readMemontMsgs()
    }

    private fun readMemontMsgs(){

        val repointTree = MessageBoxManager.getInstance(this).redpointTree

        repointTree.findRedPointById("moment")!!.invalidate(0)

    }




}