package com.github.redpointtree

import android.content.Context
import android.support.annotation.Nullable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import com.github.redpointtree.util.LogUtil

/**
 * Created by loganpluo on 2019/5/11.
 */
abstract class RedPointView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View (context, attrs, defStyleAttr) {

    var treeName:String? = null
    var redPointId:String? = null
    var redPointStyle = RedPointStyle.UNREAD_COUNT //0（默认是显示个数），1：显示红点
    private val tag = "RedPointView"

    constructor(context: Context):this(context,null, 0)

    constructor(context: Context, @Nullable attributeSet: AttributeSet):this(context, attributeSet, 0)

    init {
        if(attrs != null){
            val theme = context.theme
            val typedArray = theme.obtainStyledAttributes(attrs, R.styleable.RedPointView, defStyleAttr, 0)
            val count = typedArray.indexCount

            (0 until count)
                    .asSequence()
                    .map { typedArray.getIndex(it) }
                    .forEach { styledAttr ->
                        when(styledAttr){
                            R.styleable.RedPointView_redPointTreeName -> {
                                treeName = typedArray.getString(styledAttr)
                                LogUtil.d(tag,"treeName:$treeName")
                            }
                            R.styleable.RedPointView_redPointId -> {
                                redPointId = typedArray.getString(styledAttr)
                                LogUtil.d(tag,"redPointId:$redPointId")
                            }
                            R.styleable.RedPointView_redPointStyle -> {
                                val redPointStyleValue = typedArray.getInteger(styledAttr,0)
                                RedPointStyle.values().forEach {
                                    if(it.ordinal == redPointStyleValue){
                                        redPointStyle = it
                                    }
                                }
                            }
                        }
                    }
        }
    }

    private val redPointObserver = object: RedPointObserver {
        override fun notify(unReadCount: Int) {
            LogUtil.i(tag,"notifyView unReadCount:$unReadCount, redPointStyle:$redPointStyle")
            notifyView(unReadCount, redPointStyle)
        }
    }

    abstract fun notifyView(unReadCount: Int, redPointStyle:RedPointStyle)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        LogUtil.d(tag,"onAttachedToWindow ")
        if(TextUtils.isEmpty(treeName) || TextUtils.isEmpty(redPointId)){
            LogUtil.w(tag,"onAttachedToWindow treeName($treeName) is empty or redPointId($redPointId) is empty, add redPointObserver failed")
            return
        }
        val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree(treeName!!)
        val root = redpointTree?.findRedPointById(redPointId!!)
        root?.apply {
            addObserver(redPointObserver)
            LogUtil.d(tag,"onAttachedToWindow addObserver:$redPointObserver, and invalidateSelf")
        }?.invalidateSelf()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        LogUtil.d(tag,"onDetachedFromWindow ")
        if(TextUtils.isEmpty(treeName) || TextUtils.isEmpty(redPointId)){
            LogUtil.w(tag,"onDetachedFromWindow treeName($treeName) is empty or redPointId($redPointId) is empty, remove redPointObserver failed")
            return
        }
        val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree(treeName!!)
        val root = redpointTree?.findRedPointById(redPointId!!)
        root?.apply {
            removeObserver(redPointObserver)
            LogUtil.d(tag,"onDetachedFromWindow removeObserver:$redPointObserver")
        }
    }

}