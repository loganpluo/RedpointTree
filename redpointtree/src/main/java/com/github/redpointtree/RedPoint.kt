package com.github.redpointtree



/**
 * Created by loganpluo on 2019/4/14.
 */
open class RedPoint(tid:Int) {

    internal var parent:RedPointGroup? = null
    private var unReadCount = 0
    private var redPointObservers:MutableList<RedPointObserver> = ArrayList()

    private var id:Int = 0

    init {
        setId(tid)
    }

    fun setId(id:Int){
        this.id = id
    }

    open fun getId():Int{
        return id
    }

    open fun getUnReadCount():Int{
        return unReadCount
    }

    //只给内部用，不要改成publish，用invalidate来设置unReadCount，因为只要unReadCount变动 就要触发刷新view
    open fun setUnReadCount(unReadCount:Int){
        this.unReadCount = unReadCount
    }

    fun getParent():RedPointGroup?{
        return parent
    }

    fun removeFromParent(){
        parent = null
    }

    internal fun addParent(parent:RedPointGroup){
        this.parent = parent
    }

    /**
     * 通知自己和parent绑定的Observer
     */
    open fun invalidate(unReadCount:Int){

        if(this.unReadCount == unReadCount){
            return
        }

        this.unReadCount = unReadCount


        invalidate()
    }

    open fun invalidate(){
        invalidateSelf()
        invalidateParent()
    }

    open fun invalidateSelf(){
        //刷新当前关联的红点view
        notifyObserver()
    }

    open internal fun invalidateParent(){
        //通知parent也更新关联的红点view
        parent?.invalidateParent()

    }

    open internal fun invalidateChildren(){
        invalidateSelf()
    }

    /**
     * 只通知自己绑定的Observer
     */
    open protected fun notifyObserver(){
        redPointObservers.forEach {
            it.notify(unReadCount)
        }

    }

    open fun addObserver(redPointObserver:RedPointObserver){
        redPointObservers.add(redPointObserver)
    }

    open fun removeObserver(redPointObserver:RedPointObserver){
        redPointObservers.remove(redPointObserver)
    }

    open fun removeObserver(index:Int){
        if(redPointObservers.size > index){
            redPointObservers.removeAt(index)
        }

    }

    open fun removeAllObserver(){
        redPointObservers.clear()
    }

}