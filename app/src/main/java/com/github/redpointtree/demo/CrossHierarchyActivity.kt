package com.github.redpointtree.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.redpointtree.RedPoint
import com.github.redpointtree.RedPointGroup
import com.github.redpointtree.RedPointObserver
import com.github.redpointtree.RedPointTreeCenter
import kotlinx.android.synthetic.main.activity_cross_hierarchy.*

class CrossHierarchyActivity : AppCompatActivity() {

    val tag = "CrossHierarchyActivity|RedpointTree"

//    private val rootRedPointObserver = object:RedPointObserver{
//        override fun notify(unReadCount: Int) {
//            if(unReadCount > 0){
//                rootRedPoint.visibility = View.VISIBLE
//            }else{
//                rootRedPoint.visibility = View.INVISIBLE
//            }
//        }
//    }

    private var root: RedPoint? = null

    private var unReadMsgModel:UnReadMsgModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cross_hierarchy)

        rootView.setOnClickListener {
            val intent = Intent(this@CrossHierarchyActivity,MessageBoxActivity::class.java)
            startActivity(intent)
        }

        unReadMsgModel = UnReadMsgModel()

        unReadMsgModel?.asycRequestUnReadMsgs(object: UnReadMsgModel.UnReadMsgModelCallBack {
            override fun result(unReadMsgResult: UnReadMsgResult) {
                loadMessageBoxTree(unReadMsgResult)
            }

        })


    }



    private fun loadMessageBoxTree(unReadMsgResult: UnReadMsgResult){

        //请求红点数量

        val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree(getString(R.string.messagebox_tree))//RedpointTree(this, R.xml.messagebox)
        root = redpointTree?.findRedPointById("root")



        if(root !is RedPointGroup){
            root?.setUnReadCount(1)
        }else{
            redpointTree!!.findRedPointById(R.string.system)?.apply {
                setUnReadCount(unReadMsgResult.systemMsgCount)
            }

            redpointTree.findRedPointById(R.string.moment)?.apply {
                setUnReadCount(unReadMsgResult.momentMsgCount)
            }
        }
        root?.invalidateSelf()

        redpointTree?.print(tag)
    }

    override fun onDestroy() {
        super.onDestroy()
//        root!!.removeObserver(rootRedPointObserver)
    }


}