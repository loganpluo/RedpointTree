package com.github.redpointtree

import com.github.redpointtree.util.LogUtil

/**
 * Created by loganpluo on 2019/4/14.
 */
open class RedPointGroup(id:Int) : RedPoint(id) {

    private var childrenList = ArrayList<RedPoint>()

    fun getChildren():List<RedPoint>{
        return childrenList
    }

    override var tag: String = "RedPointGroup"

    /**
     * add 并不会执行刷新view
     */
    fun addChild(redPoint: RedPoint):Boolean{

        if(redPoint.getId() == 0){
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
        redPoint.addParent(this)

        return true
    }


    fun findRedPointById(id:Int):RedPoint?{
        //递归遍历子节点
        return findRedPointById(id,this)
    }

    fun findRedPointById(id:Int, redPoint:RedPoint):RedPoint?{
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
        redPoint.removeFromParent()

    }

    override fun invalidate(unReadCount: Int) {
        throw UnSuppotOperation("RedPointGroup invalidate to up ")
    }

    override fun invalidate(){
        invalidate(true)
    }

    override fun invalidate(needWriteCache:Boolean) {
        //也刷新下子节点
        invalidateChildren(needWriteCache)
        invalidateParent(needWriteCache)
    }



    override fun invalidateParent() {
        invalidateParent(true)
    }

    override fun invalidateParent(needWriteCache:Boolean) {
        invalidateSelf(needWriteCache)
        //通知parent也更新关联的红点view
        parent?.invalidateParent(needWriteCache)
    }

    override fun invalidateChildren() {
        invalidateChildren(true)
    }

    override fun invalidateChildren(needWriteCache:Boolean) {
        childrenList.forEach {
            it.invalidateChildren(needWriteCache)
        }
    }


     override fun invalidateSelf(needWriteCache:Boolean){
        val calculateUnReadCount = getTotalChildrenUnReadCount(this,0)

        setUnReadCount(calculateUnReadCount)

        notifyObservers(needWriteCache)
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