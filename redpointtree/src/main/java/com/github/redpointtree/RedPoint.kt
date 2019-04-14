package com.github.redpointtree



/**
 * Created by loganpluo on 2019/4/14.
 */
open class RedPoint(tid:String) {

    internal var parent:RedPointGroup? = null
    private var unReadCount = 0
    private var redPointObserver:RedPointObserver? = null

    private var id:String = ""

    init {
        setId(tid)
    }

    fun setId(id:String){
        this.id = id
    }

    open fun getId():String{
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
        redPointObserver?.notify(unReadCount)
    }

    fun setObserver(redPointObserver:RedPointObserver){
        this.redPointObserver = redPointObserver
    }

    fun removeObserver(){
        this.redPointObserver = null
    }

}