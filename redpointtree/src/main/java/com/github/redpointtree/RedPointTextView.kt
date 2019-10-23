package com.github.redpointtree

import android.content.Context
import android.support.annotation.Nullable
import android.support.v7.widget.AppCompatTextView
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.github.redpointtree.util.LogUtil

/**
 * Created by loganpluo on 2019/5/6.
 */
class RedPointTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : AppCompatTextView (context, attrs, defStyleAttr) {

    private val tag = "RedPointTextView"

    constructor(context: Context):this(context,null, 0)

    constructor(context: Context, @Nullable attributeSet: AttributeSet):this(context, attributeSet, 0)


    var treeName:String? = null
    var redPointId:String? = null
    var redPointStyle = RedPointStyle.UNREAD_COUNT //0（默认是显示个数），1：显示红点

    init {
        if(attrs != null){
            val theme = context.theme
            val typedArray = theme.obtainStyledAttributes(attrs, R.styleable.RedPointView, defStyleAttr, 0)
            val count = typedArray.indexCount

            for(i in 0 until count){
                val styledAttr = typedArray.getIndex(i)
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
            notifyView(unReadCount, redPointStyle)
        }
    }

    open fun notifyView(unReadCount: Int, redPointStyle:RedPointStyle){
        visibility = if(unReadCount > 0){
            text = if(redPointStyle == RedPointStyle.UNREAD_COUNT){
                unReadCount.toString()
            }else{
                ""
            }
            View.VISIBLE
        }else{
            View.INVISIBLE
        }
    }

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
            LogUtil.d(TAG,"onAttachedToWindow addObserver:$redPointObserver, and invalidateSelf")
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
            LogUtil.d(TAG,"onDetachedFromWindow removeObserver:$redPointObserver")
        }
    }

}