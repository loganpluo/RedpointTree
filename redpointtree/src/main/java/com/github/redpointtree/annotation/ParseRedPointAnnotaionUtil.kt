package com.github.redpointtree.annotation

import android.text.TextUtils
import com.github.redpointtree.RedPointTreeCenter
import com.github.redpointtree.util.LogUtil

/**
 * Created by loganpluo on 2019/5/18.
 */
object ParseRedPointAnnotaionUtil {

    private val tag = "ParseRedPointAnnotaionUtil"

    /**
     * 根据协议的rsp 更新红点数量
     * (1)是否更新红点协议；
     * (2)是读取字段对应redpointtreeid 和 redpointid， 设置未读数量
     * (3)刷新叶子节点 或者 整个树
     */
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

    /**
     * request 通常是消息列表第一页成功之后 清除红点
     */
    fun clear(request:Any){
        //request是不是清除红点的协议, 是否第一页
        if(request !is ClearRedPointRequest || !request.isFirstPage) return

        if( !(request.javaClass.isAnnotationPresent(BindRedPoint::class.java))){
            return
        }

        //获取绑定的节点
        val treeName =
                request.javaClass.getAnnotation(BindRedPoint::class.java).treeName

        val redPointId =
                request.javaClass.getAnnotation(BindRedPoint::class.java).redPointId

        LogUtil.i(tag,"clear request($request) treeName:$treeName, redPointId:$redPointId")

        //清除红点
        RedPointTreeCenter.getInstance().getRedPointTree(treeName)?.
                findRedPointById(redPointId)?.invalidate(0)

    }

}