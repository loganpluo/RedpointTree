package com.github.redpointtree.demo

/**
 * Created by loganpluo on 2019/4/20.
 */
class UnReadMsgModel {

    interface UnReadMsgModelCallBack {
        fun result(unReadMsgResult:UnReadMsgResult)
    }

    /**
     *
     */
    fun asycRequestUnReadMsgs(unReadMsgModelCallBack: UnReadMsgModelCallBack){
        val unReadMsgResult = UnReadMsgResult(0,12,1)
        unReadMsgModelCallBack.result(unReadMsgResult)
    }

    fun clearCacheSystemMsgCount(){
        //todo
    }

    fun clearCacheMomentMsgCount(){
        //todo
    }


}