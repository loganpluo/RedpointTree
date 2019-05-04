package com.github.redpointtree

import android.content.Context
import android.content.res.XmlResourceParser
import android.support.annotation.XmlRes
import android.text.TextUtils
import android.view.InflateException
import android.view.ViewGroup
import com.github.redpointtree.util.LogUtil
import com.tencent.mmkv.MMKV
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException


/**
 * Created by loganpluo on 2019/4/14.
 */
class RedpointTree(ctx: Context, @XmlRes val xml:Int, defaultLoadCache:Boolean = true) {
    val tag = "RedpointTree"
    private val context:Context = ctx.applicationContext
    private var rootRedPointGroup:RedPointGroup

    init {
        rootRedPointGroup = parseXml(context, xml, defaultLoadCache)
    }

    fun findRedPointById(id:String):RedPoint?{
        if(id == rootRedPointGroup.getId()){
            return rootRedPointGroup
        }
        return rootRedPointGroup.findRedPointById(id)
    }

    fun invalidate(){
        rootRedPointGroup?.invalidate()
    }

    private fun parseXml(context: Context, xml:Int, defaultLoadCache:Boolean):RedPointGroup{
        val parser = context.resources.getXml(xml)
        var eventType = parser.eventType
        LogUtil.d(tag,"parseXml start eventType:$eventType")

        var root:RedPointGroup?
        try{

            // Look for the root node.
            var type: Int = parser.next()
            while ((type) != XmlPullParser.START_TAG && type != XmlPullParser.END_DOCUMENT) {
                type = parser.next()
                LogUtil.d(tag,"parseXml type:$type, parser.name:${parser.name}")
            }

            if (type != XmlPullParser.START_TAG) {
                throw InflateException(parser.positionDescription + ": No start tag found!")
            }

            root = createRedPointGroup(parser)

            rInflateChildren(parser, root, defaultLoadCache)


        } catch (e: XmlPullParserException) {
            throw e
        } catch (e: Exception ) {
            throw e
        } finally {
            parser.close()
        }


        return root!!
    }

    private fun createRedPointGroup(parser:XmlPullParser):RedPointGroup{
        var redPointGroup:RedPointGroup? = null
        for(i in 0 until parser.attributeCount){
            val attributeName = parser.getAttributeName(i)
            val attributeValue = parser.getAttributeValue(i)
            if("id" == attributeName && !TextUtils.isEmpty(attributeValue)){
                redPointGroup = RedPointGroup(attributeValue)
            }
            LogUtil.d(tag,"createRedPointGroup i:$i, attributeName:$attributeName, attributeValue:$attributeValue")
        }

        if(redPointGroup != null) return redPointGroup

        throw CreateRedPointGroupException("createRedPointGroup failed, id is empty")
    }

    class CreateRedPointGroupException(msg:String):Exception(msg)

    private fun createRedPoint(parser:XmlPullParser, defaultLoadCache:Boolean):RedPoint?{
        var redPoint:RedPoint? = null

        for(i in 0 until parser.attributeCount){
            val attributeName = parser.getAttributeName(i)
            val attributeValue = parser.getAttributeValue(i)

            if("id" == attributeName && !TextUtils.isEmpty(attributeValue)){
                if(redPoint == null){
                    redPoint = RedPoint(attributeValue)
                }
                redPoint.setId(attributeValue)
            }

            if("needCache" == attributeName){
                val needCache = "true" == (attributeValue)

                if(redPoint == null){
                    redPoint = RedPoint("")
                }

                if(needCache){

                    var cacheUnReadCount = 0
                    if(defaultLoadCache){
                        val cacheKey = redPoint.getCacheKey()
                        cacheUnReadCount = MMKV.defaultMMKV().getInt(cacheKey,0)

                        redPoint.setUnReadCount(cacheUnReadCount)
                        LogUtil.d(tag,"createRedPoint cacheKey:$cacheKey, cacheUnReadCount:$cacheUnReadCount")
                    }

                    redPoint.addObserver(object:RedPointWriteCacheObserver{
                        var preUnReadCount = cacheUnReadCount
                        override fun notify(unReadCount: Int) {

                            val newCacheKey = redPoint.getCacheKey()
                            if(preUnReadCount != unReadCount && !TextUtils.isEmpty(newCacheKey)){
                                LogUtil.d(tag,"createRedPoint notify cacheKey:$newCacheKey, unReadCount:$unReadCount")
                                MMKV.defaultMMKV().putInt(newCacheKey,unReadCount)
                            }
                        }
                    })

                }
            }
            LogUtil.d(tag,"createRedPoint attributeName:$attributeName, attributeValue:$attributeValue")

        }
        //todo throw
        return redPoint
    }



    private fun rInflateChildren(parser:XmlPullParser, parent:RedPointGroup, defaultLoadCache:Boolean){

        val depth = parser.depth
        var type: Int = parser.next()

        while ((type != XmlPullParser.END_TAG || parser.depth > depth) && type != XmlPullParser.END_DOCUMENT) {

            if (type != XmlPullParser.START_TAG) {
                type = parser.next()
                continue
            }

            val name = parser.name
            LogUtil.d(tag,"rInflateChildren name:$name, type:$type")
            //继续递归添加
            if("RedPointGroup" == name){
                val currentRedPoint = createRedPointGroup(parser)
                if(currentRedPoint != null){
                    parent.addChild(currentRedPoint)
                    rInflateChildren(parser, parent, defaultLoadCache)
                }
            }else if("RedPoint" == name){
                val currentRedPoint = createRedPoint(parser, defaultLoadCache)
                if(currentRedPoint != null){
                    parent.addChild(currentRedPoint)
                }
            }

            type = parser.next()
        }

    }

    fun loadCache(){
        loadCache(rootRedPointGroup)
        rootRedPointGroup.invalidate(false)
    }

    private fun loadCache(redPoint:RedPoint){
        if(redPoint is RedPointGroup){
            redPoint.getChildren().forEach {
                loadCache(it)
            }
            return
        }
        val cacheUnReadCount = MMKV.defaultMMKV().getInt(redPoint.getCacheKey(),0)
        if(cacheUnReadCount != 0){
            LogUtil.d(tag,"id:${redPoint.getId()}, loadCache cacheUnReadCount:$cacheUnReadCount")
            redPoint.setUnReadCount(cacheUnReadCount)
        }

    }

    fun clearUnReadCount(needWriteCache:Boolean){
        setUnReadCount(rootRedPointGroup, 0)

        rootRedPointGroup.invalidate(needWriteCache)
    }

    fun setUnReadCount(redPoint:RedPoint, unReadCount:Int){
        if(redPoint is RedPointGroup){
            redPoint.getChildren().forEach {
                setUnReadCount(it,unReadCount)
            }
            return
        }

        redPoint.setUnReadCount(unReadCount)

    }


    fun print(){
        //todo



    }

}