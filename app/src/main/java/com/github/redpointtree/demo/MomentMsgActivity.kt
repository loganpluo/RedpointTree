package com.github.redpointtree.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.redpointtree.RedPointTreeCenter

class MomentMsgActivity : AppCompatActivity() {

    val tag = "MomentMsgActivity|RedpointTree"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moment_msg)

        readMemontMsgs()
    }

    private fun readMemontMsgs(){

        val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree("messagebox")

        redpointTree!!.findRedPointById(R.string.moment)!!.invalidate(0)

    }




}