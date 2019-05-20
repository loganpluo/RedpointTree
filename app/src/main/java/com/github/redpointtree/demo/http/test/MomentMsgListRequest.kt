package com.github.redpointtree.demo.http.test

import com.github.redpointtree.annotation.BindRedPoint
import com.github.redpointtree.annotation.ClearRedPointRequest

/**
 * Created by loganpluo on 2019/5/20.
 */
@BindRedPoint(treeName = "messagebox", redPointId = "messagebox_moment")
class MomentMsgListRequest : ClearRedPointRequest {

    var offset = 0

    override fun isFirstPage(): Boolean {
        return offset == 0
    }
}