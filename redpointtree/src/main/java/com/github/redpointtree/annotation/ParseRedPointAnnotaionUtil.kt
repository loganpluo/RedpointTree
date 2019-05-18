package com.github.redpointtree.annotation

import android.text.TextUtils
import com.github.redpointtree.RedPointTreeCenter
import com.github.redpointtree.util.LogUtil

/**
 * Created by loganpluo on 2019/5/18.
 */
object ParseRedPointAnnotaionUtil {

    private val tag = "ParseRedPointAnnotaionUtil"

    fun invalidate(rsp: Any){
        try{

            //判断是不是RedPointCountRsp
            if( !(rsp.javaClass.isAnnotationPresent(RedPointCountRsp::class.java))){
                return
            }

            val invalidateType =
                    rsp.javaClass.getAnnotation(RedPointCountRsp::class.java).invalidateType

            val treeName = rsp.javaClass.getAnnotation(RedPointCountRsp::class.java).treeName

            LogUtil.i(tag,"invalidate rsp, invalidateType:$invalidateType treeName:$treeName")

            //再遍历每个字段，获取 BindRedPoint， 获取值刷新
            rsp.javaClass.declaredFields.forEach {

                if(it.isAnnotationPresent(BindRedPoint::class.java)){
                    it.isAccessible = true
                    val unReadCount = it.getInt(rsp)

                    var pointTreeName =
                            it.getAnnotation(BindRedPoint::class.java).treeName

                    if(TextUtils.isEmpty(pointTreeName)){
                        pointTreeName = treeName
                    }

                    val redPointId =
                            it.getAnnotation(BindRedPoint::class.java).redPointId

                    val redPoint =  RedPointTreeCenter.getInstance().
                            getRedPointTree(pointTreeName)?.
                            findRedPointById(redPointId)

                    LogUtil.i(tag,"invalidate rsp parse field, ${it.name} pointTreeName:$pointTreeName, " +
                            "redPointId:$redPointId, unReadCount:$unReadCount, invalidateType:$invalidateType, redPoint:$redPoint")

                    redPoint?.setUnReadCount(unReadCount)

                    if(invalidateType == InvalidateType.Point){
                        LogUtil.i(tag,"invalidate rsp parse field, treeName:$treeName, redPointId:$redPointId ")
                        redPoint?.invalidate()
                    }

                }
            }

            if(invalidateType == InvalidateType.Tree){
                LogUtil.i(tag,"invalidate treeName:$treeName ")
                RedPointTreeCenter.getInstance().
                        getRedPointTree(treeName)?.invalidate()
            }

        }catch (e:Throwable){
            LogUtil.e(tag,"invalidate exception ${e.message}")
        }



    }

}