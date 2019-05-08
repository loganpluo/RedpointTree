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


    private var treeName:String? = null
    private var redPointId:String? = null
    private var redPointStyle = 0 //0（默认是显示个数），1：显示红点

    init {
        if(attrs != null){
            val theme = context.theme
            val typedArray = theme.obtainStyledAttributes(attrs, R.styleable.RedPointTextView, defStyleAttr, 0)
            val count = typedArray.indexCount

            for(i in 0 until count){
                val styledAttr = typedArray.getIndex(i)
                when(styledAttr){
                    R.styleable.RedPointTextView_redPointTreeName -> {
                        treeName = typedArray.getString(i)
                        LogUtil.d(tag,"treeName:$treeName")
                    }
                    R.styleable.RedPointTextView_redPointId -> {
                        redPointId = typedArray.getString(i)
                        LogUtil.d(tag,"redPointId:$redPointId")
                    }
                    R.styleable.RedPointTextView_redPointStyle -> {
                        redPointStyle = typedArray.getInteger(i,0)
                    }
                }
            }
        }
    }

    private val redPointObserver = object: RedPointObserver {
        override fun notify(unReadCount: Int) {
            visibility = if(unReadCount > 0){
                text = if(redPointStyle == 0){
                    unReadCount.toString()
                }else{
                    ""
                }
                View.VISIBLE
            }else{
                View.INVISIBLE
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(tag,"onAttachedToWindow ")
        if(TextUtils.isEmpty(treeName) || TextUtils.isEmpty(redPointId)){
            LogUtil.w(tag,"onAttachedToWindow treeName is empty or redPointId is empty, add redPointObserver failed")
            return
        }
        val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree(treeName!!)
        val root = redpointTree?.findRedPointById(redPointId!!)
        root?.apply {
            addObserver(redPointObserver)
        }?.invalidateSelf()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d(tag,"onDetachedFromWindow ")
        if(TextUtils.isEmpty(treeName) || TextUtils.isEmpty(redPointId)){
            LogUtil.w(tag,"onDetachedFromWindow treeName is empty or redPointId is empty, remove redPointObserver failed")
            return
        }
        val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree(treeName!!)
        val root = redpointTree?.findRedPointById(redPointId!!)
        root?.apply {
            removeObserver(redPointObserver)
        }        
    }

}