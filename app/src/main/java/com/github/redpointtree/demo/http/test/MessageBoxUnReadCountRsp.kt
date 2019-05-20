package com.github.redpointtree.demo.http.test

import com.github.redpointtree.annotation.BindRedPoint
import com.github.redpointtree.annotation.InvalidateType
import com.github.redpointtree.annotation.RedPointCountRsp

/**
 * Created by loganpluo on 2019/5/20.
 */
@RedPointCountRsp(treeName = "messagebox",invalidateType = InvalidateType.Tree)
class MessageBoxUnReadCountRsp(var code:Int = 0,

                               @BindRedPoint(redPointId = "messagebox_system")
                               var systemMsgCount:Int = 0,

                               @BindRedPoint(redPointId = "messagebox_moment")
                               var momentMsgCount:Int = 0)