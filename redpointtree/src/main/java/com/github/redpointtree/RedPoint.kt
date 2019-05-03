package com.github.redpointtree

import android.os.Looper
import com.github.redpointtree.util.SafeIterableMap


/**
 * Created by loganpluo on 2019/4/14.
 */
open class RedPoint(tid:Int) {

    companion object {
        val START_VERSION = -1
    }

    internal var parent:RedPointGroup? = null
    private var unReadCount = 0
//    private var redPointObservers:MutableList<RedPointObserver> = ArrayList()

    private val observers = SafeIterableMap<RedPointObserver, VersionObserver>()

    private var id:Int = 0

    private var version = START_VERSION

    private var mDispatchingValue: Boolean = false
    private var mDispatchInvalidated: Boolean = false

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

    open fun setUnReadCount(unReadCount:Int){
        assertMainThread("setUnReadCount")
        if(this.unReadCount != unReadCount){
            this.unReadCount = unReadCount
            version++
        }

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

//        if(this.unReadCount == unReadCount){
//            return
//        }
//
//        this.unReadCount = unReadCount

        setUnReadCount(unReadCount)

        invalidate()
    }

    open fun invalidate(){
        invalidateSelf()
        invalidateParent()
    }

    open fun invalidateSelf(){
        //刷新当前关联的红点view
        notifyObservers()
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
    open protected fun notifyObservers(){
        assertMainThread("notifyObservers")
//        redPointObservers.forEach {
//            it.notify(unReadCount)
//        }
        //这一段是参考LiveData源码，为了防止多次调用？
        if (mDispatchingValue) {
            mDispatchInvalidated = true
            return
        }
        mDispatchingValue = true
        do {
            mDispatchInvalidated = false
            val iterator = observers.iteratorWithAdditions()
            while (iterator.hasNext()) {
                considerNotify(iterator.next().value)
                if (mDispatchInvalidated) {
                    break
                }
            }
        } while (mDispatchInvalidated)
        mDispatchingValue = false

    }

    private fun considerNotify(observer: VersionObserver) {

        if (observer.lastVersion >= version) {
            return
        }
        observer.lastVersion = version



        observer.redPointObserver.notify(unReadCount)
    }

    open fun addObserver(redPointObserver:RedPointObserver){
        val versionObserver = VersionObserver(redPointObserver)
        val existing = observers.putIfAbsent(redPointObserver,versionObserver)
        if(existing != null){
            return
        }
//        redPointObservers.add(redPointObserver)
    }

    open fun removeObserver(redPointObserver:RedPointObserver){
//        redPointObservers.remove(redPointObserver)
        assertMainThread("removeObserver")
        val removed = observers.remove(redPointObserver) ?: return
    }

//    open fun removeObserver(index:Int){
//        if(redPointObservers.size > index){
//            redPointObservers.removeAt(index)
//        }
//
//    }

    open fun removeAllObserver(){
//        redPointObservers.clear()

        assertMainThread("removeObservers")
        for (entry in observers) {
            removeObserver(entry.key)
        }

    }

    private fun assertMainThread(methodName: String) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("Cannot invoke RedPoint." + methodName + " on a background"
                    + " thread")
        }
    }

}