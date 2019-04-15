package com.github.redpointtree

import android.text.TextUtils
import com.github.redpointtree.util.LogUtil

/**
 * Created by loganpluo on 2019/4/14.
 */
open class RedPointGroup(id:String) : RedPoint(id) {
    private val tag = "RedPointGroup"
    private var childrenList = ArrayList<RedPoint>()
    private var childrenMap = HashMap<String, RedPoint>()

    /**
     * add 并不会执行刷新view
     */
    fun addChild(redPoint: RedPoint):Boolean{

        if(TextUtils.isEmpty(redPoint.getId())){
            LogUtil.e(tag,"addChild fail, redPoint.getId is empty")
            return false
        }

//        //删除原来的
//        val findRedPoint = findRedPointById(redPoint.getId())
//        if(findRedPoint != null){
//            LogUtil.i(tag,"addChild remove pre redpoint, redPoint.getId:${redPoint.getId()}")
//            removeChild(findRedPoint)
//        }

        childrenList.add(redPoint)
        childrenMap.put(redPoint.getId(),redPoint)
        redPoint.addParent(this)

        return true
    }


    fun findRedPointById(id:String):RedPoint?{
        //递归遍历子节点
        return findRedPointById(id,this)
    }

    fun findRedPointById(id:String, redPoint:RedPoint):RedPoint?{
        if(id == redPoint.getId()){
            return redPoint
        }

        if(redPoint is RedPointGroup){
            redPoint.childrenList.forEach {
                if(id == it.getId()){
                    return it
                }else if(it is RedPointGroup){
                    val findRedPoint = findRedPointById(id,it)
                    if(findRedPoint != null){
                        return findRedPoint
                    }
                }
            }
        }

        return null
    }

    fun removeChild(redPoint: RedPoint){
        childrenList.remove(redPoint)
        childrenMap.remove(redPoint.getId())
        redPoint.removeFromParent()

    }

    override fun invalidate(unReadCount: Int) {
        throw UnSuppotOperation("RedPointGroup invalidate to up ")
    }

    override fun invalidate() {
        //也刷新下子节点
        invalidateChildren()
        invalidateParent()
    }



    override fun invalidateParent() {
        invalidateSelf()
        //通知parent也更新关联的红点view
        parent?.invalidateParent()
    }

    override fun invalidateChildren() {
        childrenList.forEach {
            it.invalidateChildren()
        }
    }


     override fun invalidateSelf(){
        val calculateUnReadCount = getTotalChildrenUnReadCount(this,0)
        if(calculateUnReadCount == getUnReadCount()){
            return
        }

        setUnReadCount(calculateUnReadCount)


        notifyObserver()
    }

    //todo check 调用这是不是 自己，红点viewgroup的未读个数是要getTotalChildrenUnReadCount来获取的
    override fun setUnReadCount(unReadCount: Int) {
        super.setUnReadCount(unReadCount)
    }


    fun getTotalChildrenUnReadCount(redPoint:RedPoint, totalUnReadCount:Int):Int{
        var totalUnReadCountCopy = totalUnReadCount
        if(redPoint is RedPointGroup){//如果是父亲节点，则继续遍历

            childrenList.forEach {
                totalUnReadCountCopy += getTotalChildrenUnReadCount(it,totalUnReadCountCopy)
            }

            return totalUnReadCountCopy
        }

        return redPoint.getUnReadCount()
    }




    class UnSuppotOperation(msg:String): Exception(msg)

}