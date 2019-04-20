package com.github.redpointtree.demo

import android.content.Context
import com.github.redpointtree.RedPointObserver
import com.github.redpointtree.RedpointTree

/**
 * Created by loganpluo on 2019/4/15.
 */
class MessageBoxManager(context: Context) {

    val redpointTree: RedpointTree = RedpointTree(context, R.xml.messagebox)

    companion object {
        private var instance:MessageBoxManager? = null

        fun getInstance(context: Context):MessageBoxManager{
            if(instance == null){
                instance = MessageBoxManager(context)
            }
            return instance!!
        }

    }

    init {
        bindClearCacheObserver()
    }

    private fun bindClearCacheObserver(){
        redpointTree.findRedPointById(R.id.system)?.addObserver(object:RedPointObserver{
            override fun notify(unReadCount: Int) {

            }

        })

        redpointTree.findRedPointById(R.id.moment)?.addObserver(object:RedPointObserver{
            override fun notify(unReadCount: Int) {

            }

        })

    }


}