package com.github.redpointtree.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.redpointtree.RedPoint
import com.github.redpointtree.RedPointGroup
import com.github.redpointtree.RedPointObserver
import kotlinx.android.synthetic.main.activity_redpoint_tree_in_simpleactivity.*

class RedPointTreeInSimpleActivity : AppCompatActivity() {

    val tag = "MainActivity|RedpointTree"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redpoint_tree_in_simpleactivity)


        testRedPointTreeInSimpleActivity()

    }

    private fun testRedPointTreeInSimpleActivity(){
        val rootRedPointView = findViewById<View>(R.id.rootRedPoint)
        val root = RedPointGroup(R.id.root)
        root.addObserver(object: RedPointObserver {
            override fun notify(unReadCount: Int) {
                if(unReadCount > 0){
                    rootRedPointView.visibility = View.VISIBLE
                }else{
                    rootRedPointView.visibility = View.INVISIBLE
                }
            }

        })

        val level11RedPointView = findViewById<View>(R.id.level11RedPoint)
        val level11 = RedPoint(R.id.system)
        level11.addObserver(object: RedPointObserver {
            override fun notify(unReadCount: Int) {
                if(unReadCount > 0){
                    level11RedPointView.visibility = View.VISIBLE
                }else{
                    level11RedPointView.visibility = View.INVISIBLE
                }
            }
        })
        level1_1_text.setOnClickListener {
            level11.invalidate(0)
        }
        level11.setUnReadCount(2)
        root.addChild(level11)



        val level12RedPointView = findViewById<View>(R.id.level12RedPoint)
        val level12 = RedPoint(R.id.moment)
        level12.addObserver(object: RedPointObserver {
            override fun notify(unReadCount: Int) {
                if(unReadCount > 0){
                    level12RedPointView.visibility = View.VISIBLE
                }else{
                    level12RedPointView.visibility = View.INVISIBLE
                }
            }
        })
        level1_2_text.setOnClickListener {
            level12.invalidate(0)
        }
        level12.setUnReadCount(4)
        root.addChild(level12)


        root.invalidate()

        //如何避免初始化的时候 多次刷新；
        //给好值之后，再从树顶到地步一直刷新
        //addChild时候怎么动态刷新

        //viewtree怎么刷新，会存在多次刷新吗


    }
}