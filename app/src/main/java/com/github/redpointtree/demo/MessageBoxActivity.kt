package com.github.redpointtree.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.redpointtree.RedPointGroup
import com.github.redpointtree.RedPointTreeCenter
import kotlinx.android.synthetic.main.activity_messagebox.*

class MessageBoxActivity : AppCompatActivity() {

    val tag = "MessageBoxActivity|RedpointTree"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messagebox)

        val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree(getString(R.string.messagebox_tree))
        val root = redpointTree?.findRedPointById("root")
        if(root !is RedPointGroup){
            root?.apply {
                setUnReadCount(0)
                invalidate()
            }
            return
        }


        systemRedPointText.setOnClickListener {
            RouteUtils.dispatch(this,"${getString(R.string.app_page_scheme)}://system_msglist")
        }

        momentRedPointText.setOnClickListener {
            RouteUtils.dispatch(this,"${getString(R.string.app_page_scheme)}://moment_msglist")
        }

    }



}