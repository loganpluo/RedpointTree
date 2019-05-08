package com.github.redpointtree.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.redpointtree.RedPoint
import com.github.redpointtree.RedPointGroup
import com.github.redpointtree.RedPointObserver
import com.github.redpointtree.RedPointTreeCenter
import kotlinx.android.synthetic.main.activity_messagebox.*

class MessageBoxActivity : AppCompatActivity() {

    val tag = "MessageBoxActivity|RedpointTree"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messagebox)

        val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree(getString(R.string.messagebox_tree))//RedpointTree(this, R.xml.messagebox)
        val root = redpointTree?.findRedPointById("root")
        if(root !is RedPointGroup){
            root?.apply {
                setUnReadCount(0)
                invalidate()
            }
            return
        }


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

        val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree(getString(R.string.messagebox_tree))
        systemRedPoint = redpointTree!!.findRedPointById(R.string.system)

        systemRedPoint!!.apply {
            addObserver(systemRedPointObserver)
        }.invalidateSelf()

        momentRedPoint = redpointTree.findRedPointById(R.string.moment)!!
        momentRedPoint!!.apply {
            addObserver(momentRedPointObserver)
        }.invalidateSelf()


    }

    override fun onDestroy() {
        super.onDestroy()

        systemRedPoint?.removeObserver(systemRedPointObserver)
        momentRedPoint?.removeObserver(momentRedPointObserver)
    }


}