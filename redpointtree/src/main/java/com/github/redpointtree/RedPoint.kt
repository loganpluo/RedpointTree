package com.github.redpointtree

import android.os.Looper
import android.text.TextUtils
import com.github.redpointtree.util.LogUtil
import com.github.redpointtree.util.SafeIterableMap


/**
 * Created by loganpluo on 2019/4/14.
 */
open class RedPoint(tid:String) {

    companion object {
        val START_VERSION = -1
    }

    internal var parent:RedPointGroup? = null
    private var unReadCount = 0

    private val observers = SafeIterableMap<RedPointObserver, VersionObserver>()

    private var id:String? = null

    private var version = START_VERSION

    private var mDispatchingValue: Boolean = false
    private var mDispatchInvalidated: Boolean = false

    private var isMuteToParent = false//对父亲节点是不是忽略，不参与红点计算，为了支持会话里面的静音功能

    var TAG = "RedPoint"

    var tag:String? = null

    init {
        setId(tid)
    }

    fun setId(id:String){
        this.id = id
    }

    open fun getId():String?{
        return id
    }

    open fun setIsMuteToParent(isMuteToParent:Boolean){
        this.isMuteToParent = isMuteToParent
    }

    open fun isMuteToParent():Boolean{
        return isMuteToParent
    }

    fun getCacheKey():String{
        if(TextUtils.isEmpty(id)) return ""

        val cacheKey = StringBuilder()

        val preKey = RedPointConfig.redPointCachePreKey?.getRedPointCachePreKey()?:""
        if(!TextUtils.isEmpty(preKey)){
            cacheKey.append(preKey)
            cacheKey.append("&")
        }

        cacheKey.append(id)

        return cacheKey.toString()
    }

    open fun getUnReadCount():Int{
        return unReadCount
    }

    open fun setUnReadCount(unReadCount:Int){
        assertMainThread("setUnReadCount")
        if(this.unReadCount != unReadCount){
            this.unReadCount = unReadCount
            version++
            LogUtil.i(TAG,"setUnReadCount id:$id, setUnReadCount($unReadCount:Int) ")
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

        if(this.unReadCount == unReadCount){
            return
        }

        setUnReadCount(unReadCount)

        invalidate()
    }

    open fun invalidate(){
        invalidate(true)
    }

    open fun invalidate(needWriteCache:Boolean){
        invalidateSelf(needWriteCache)
        invalidateParent()
    }

    open fun invalidateSelf(){
        invalidateSelf(true)
    }

    open fun invalidateSelf(needWriteCache:Boolean){
        //刷新当前关联的红点view
        notifyObservers(needWriteCache)
    }

    open fun invalidateParent(){
        invalidateParent(true)
    }

    open fun invalidateParent(needWriteCache:Boolean){
        //通知parent也更新关联的红点view
        parent?.invalidateParent(needWriteCache)
    }

    open fun invalidateChildren(){
        invalidateChildren(true)
    }

    open fun invalidateChildren(needWriteCache:Boolean){
        invalidateSelf(needWriteCache)
    }

    /**
     * 只通知自己绑定的Observer
     */
    open protected fun notifyObservers(needWriteCache:Boolean){
        assertMainThread("notifyObservers")
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
                considerNotify(iterator.next().value, needWriteCache)
                if (mDispatchInvalidated) {
                    break
                }
            }
        } while (mDispatchInvalidated)
        mDispatchingValue = false

    }

    private fun considerNotify(observer: VersionObserver,needWriteCache:Boolean) {

        if (observer.lastVersion >= version) {
            return
        }

        //不需要写缓存,比如切换游客态的时候
        if(!needWriteCache && (observer.redPointObserver is RedPointWriteCacheObserver)){
            LogUtil.i(TAG,"considerNotify id:$id,  not  needWriteCache")
            return
        }

        observer.lastVersion = version

        LogUtil.i(TAG,"considerNotify id:$id, unReadCount:$unReadCount, ${observer.redPointObserver}")
        observer.redPointObserver.notify(unReadCount)
    }

    open fun addObserver(redPointObserver:RedPointObserver){
        val versionObserver = VersionObserver(redPointObserver)
        val existing = observers.putIfAbsent(redPointObserver,versionObserver)
        if(existing != null){
            return
        }
    }

    open fun removeObserver(redPointObserver:RedPointObserver){
        assertMainThread("removeObserver")
        observers.remove(redPointObserver)
    }


    open fun removeAllObserver(){
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

    override fun toString(): String {
        return "RedPoint(unReadCount=$unReadCount, id=$id)"
    }


}