package com.github.redpointtree.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.redpointtree.RedPoint
import com.github.redpointtree.RedPointGroup
import com.github.redpointtree.RedPointObserver
import com.github.redpointtree.RepointTree
import kotlinx.android.synthetic.main.activity_cross_hierarchy.*

class CrossHierarchyActivity : AppCompatActivity() {

    val tag = "CrossHierarchyActivity|RepointTree"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cross_hierarchy)

        rootView.setOnClickListener {
            val intent = Intent(this@CrossHierarchyActivity,MessageBoxActivity::class.java)
            startActivity(intent)
        }

        loadMessageBoxTree()
    }

    private val rootRedPointObserver = object:RedPointObserver{
        override fun notify(unReadCount: Int) {
            if(unReadCount > 0){
                rootRedPoint.visibility = View.VISIBLE
            }else{
                rootRedPoint.visibility = View.INVISIBLE
            }
        }
    }

    private var root: RedPoint? = null

    private fun loadMessageBoxTree(){

        val repointTree = MessageBoxManager.getInstance(this).repointTree//RepointTree(this, R.xml.messagebox)
        repointTree.findRedPointById("system")!!.apply {
            setUnReadCount(12)
        }

        repointTree.findRedPointById("moment")!!.apply {
            setUnReadCount(1)
        }

        root = repointTree.findRedPointById("root")!!
        root!!.apply {
            setObserver(rootRedPointObserver)
        }.invalidateSelf()


    }

    override fun onDestroy() {
        super.onDestroy()
        root!!.removeObserver()
    }


}