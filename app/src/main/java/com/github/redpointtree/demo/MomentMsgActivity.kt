package com.github.redpointtree.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.redpointtree.RedPointObserver
import com.github.redpointtree.RepointTree

class MomentMsgActivity : AppCompatActivity() {

    val tag = "MomentMsgActivity|RepointTree"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moment_msg)

        readMemontMsgs()
    }

    private fun readMemontMsgs(){

        val repointTree = MessageBoxManager.getInstance(this).repointTree

        repointTree.findRedPointById("moment")!!.invalidate(0)

    }




}