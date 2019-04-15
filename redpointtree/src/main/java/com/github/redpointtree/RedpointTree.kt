package com.github.redpointtree

import android.content.Context
import android.content.res.XmlResourceParser
import android.support.annotation.XmlRes
import android.text.TextUtils
import android.view.InflateException
import android.view.ViewGroup
import com.github.redpointtree.util.LogUtil
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException


/**
 * Created by loganpluo on 2019/4/14.
 */
class RedpointTree(ctx: Context, @XmlRes val xml:Int) {
    val tag = "RedpointTree"
    private val context:Context = ctx.applicationContext
    private var rootRedPointGroup:RedPointGroup

    init {
        rootRedPointGroup = parseXml(context, xml)
    }

    fun findRedPointById(id:Int):RedPoint?{
        if(id == rootRedPointGroup.getId()){
            return rootRedPointGroup
        }
        return rootRedPointGroup.findRedPointById(id)
    }



    private fun parseXml(context: Context, xml:Int):RedPointGroup{
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

            rInflateChildren(parser,root)


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
                redPointGroup = RedPointGroup(attributeValue.removePrefix("@").toInt())
            }
//            LogUtil.d(tag,"createRedPointGroup i:$i, attributeName:$attributeName, attributeValue:$attributeValue")

        }

        if(redPointGroup != null) return redPointGroup

        throw CreateRedPointGroupException("createRedPointGroup failed, id is empty")
    }

    class CreateRedPointGroupException(msg:String):Exception(msg)

    private fun createRedPoint(parser:XmlPullParser):RedPoint?{
        var redPoint:RedPoint? = null
        for(i in 0 until parser.attributeCount){
            val attributeName = parser.getAttributeName(i)
            val attributeValue = parser.getAttributeValue(i)
            if("id" == attributeName && !TextUtils.isEmpty(attributeValue)){
                redPoint = RedPoint(attributeValue.removePrefix("@").toInt())
            }
//            LogUtil.d(tag,"createRedPointGroup i:$i, attributeName:$attributeName, attributeValue:$attributeValue")

        }
        //todo throw
        return redPoint
    }

    private fun rInflateChildren(parser:XmlPullParser, parent:RedPointGroup){

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
                    rInflateChildren(parser, parent)
                }
            }else if("RedPoint" == name){
                val currentRedPoint = createRedPoint(parser)
                if(currentRedPoint != null){
                    parent.addChild(currentRedPoint)
                }
            }

            type = parser.next()
        }

    }

    fun print(){

    }

}