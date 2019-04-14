package com.github.redpointtree.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.redpointtree.RedPoint
import com.github.redpointtree.RedPointGroup
import com.github.redpointtree.RedPointObserver
import com.github.redpointtree.RepointTree
import com.github.redpointtree.util.LogUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val tag = "MainActivity|RepointTree"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val repointTree = RepointTree(this, R.xml.messagebox)

        val myGroupItem = MyGroupItem("myGroupItem")

        val item = MyItem("MyItem")

        LogUtil.d(tag," GroupItem.id:${myGroupItem.getId()}, item.getId:${item.getId()}")

        testRedPointTreeInSimpleActivity()

    }

    private fun testRedPointTreeInSimpleActivity(){
        val rootRedPointView = findViewById<View>(R.id.rootRedPoint)
        val root = RedPointGroup("root")
        root.setObserver(object:RedPointObserver{
            override fun notify(unReadCount: Int) {
                if(unReadCount > 0){
                    rootRedPointView.visibility = View.VISIBLE
                }else{
                    rootRedPointView.visibility = View.INVISIBLE
                }
            }

        })

        val level11RedPointView = findViewById<View>(R.id.level11RedPoint)
        val level11 = RedPoint("level11")
        level11.setObserver(object:RedPointObserver{
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
        val level12 = RedPoint("level121")
        level12.setObserver(object:RedPointObserver{
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
