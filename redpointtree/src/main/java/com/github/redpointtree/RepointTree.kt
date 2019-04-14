package com.github.redpointtree

import android.content.Context
import android.content.res.XmlResourceParser
import android.support.annotation.XmlRes
import com.github.redpointtree.util.LogUtil


/**
 * Created by loganpluo on 2019/4/14.
 */
class RepointTree(ctx: Context,@XmlRes val xml:Int) {
    val tag = "RepointTree"
    private val context:Context = ctx.applicationContext


    init {
        parseXml(context, xml)
    }

    private fun parseXml(context: Context, xml:Int){
        val xmlParser = context.resources.getXml(xml)
        var eventType = xmlParser.eventType
        LogUtil.d(tag,"parseXml start eventType:$eventType")

        //怎么解析的
        //怎么便利tree的，递归是吧

        //不是文件结尾就继续解析
        while (eventType != XmlResourceParser.END_DOCUMENT) {

//            when (eventType) {
//
//            //文件的内容的起始标签开始，注意这里的起始标签是ThirdPartyUsers.xml文件
//            //里面<ThirdPartyUsers>标签下面的第一个标签ThirdPartyUser
//                XmlResourceParser.START_TAG -> {
//                    val tagName = xmlParser.getName()
//                    if (tagName.endsWith("ThirdPartyUser")) {
//                        val user = User()
//                        user.user = xmlParser.getAttributeValue(null, "name")
//                        user.age = xmlParser.getAttributeValue(null, "age")
//                        user.location = xmlParser.getAttributeValue(null, "location")
//                        users.add(user)
//                    }
//                }
//
//                XmlResourceParser.END_TAG -> {
//                }
//
//                XmlResourceParser.TEXT -> {
//                }
//                else -> {
//                }
//            }

            LogUtil.d(tag,"parseXml eventType:$eventType , name:${xmlParser.name}")

            //如果是NodeGroup 则递归便利

            eventType = xmlParser.next()



        }
        xmlParser.close()


    }

    fun print(){

    }

}