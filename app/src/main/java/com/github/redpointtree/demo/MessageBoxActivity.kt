package com.github.redpointtree.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.redpointtree.RedPoint
import com.github.redpointtree.RedPointObserver
import kotlinx.android.synthetic.main.activity_messagebox.*

class MessageBoxActivity : AppCompatActivity() {

    val tag = "MessageBoxActivity|RepointTree"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messagebox)


        systemRedPointText.setOnClickListener {
            val intent = Intent(this@MessageBoxActivity,SystemMsgActivity::class.java)
            startActivity(intent)
        }

        momentRedPointText.setOnClickListener {
            val intent = Intent(this@MessageBoxActivity,MomentMsgActivity::class.java)
            startActivity(intent)
        }

        loadMessageBoxTree()
    }

    private val systemRedPointObserver = object:RedPointObserver{
        override fun notify(unReadCount: Int) {
            if(unReadCount > 0){
                systemRedPointView.visibility = View.VISIBLE
            }else{
                systemRedPointView.visibility = View.INVISIBLE
            }
        }

    }

    private val momentRedPointObserver = object:RedPointObserver{
        override fun notify(unReadCount: Int) {
            if(unReadCount > 0){
                momentRedPointView.visibility = View.VISIBLE
            }else{
                momentRedPointView.visibility = View.INVISIBLE
            }
        }

    }

    private var systemRedPoint: RedPoint? = null
    private var momentRedPoint: RedPoint? = null

    private fun loadMessageBoxTree(){

        val repointTree = MessageBoxManager.getInstance(this).repointTree
        systemRedPoint = repointTree.findRedPointById("system")

        systemRedPoint!!.apply {
            setObserver(systemRedPointObserver)
        }.invalidateSelf()

        momentRedPoint = repointTree.findRedPointById("moment")!!
        momentRedPoint!!.apply {
            setObserver(momentRedPointObserver)
        }.invalidateSelf()


    }

    override fun onDestroy() {
        super.onDestroy()

        systemRedPoint!!.removeObserver()
        momentRedPoint!!.removeObserver()
    }


}