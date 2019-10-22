package com.github.redpointtree.annotation

import android.support.annotation.Keep

/**
 * Created by loganpluo on 2019/5/18.
 */
@Keep
enum class InvalidateType {

    Point,//每解析完字段，设置@BindRedPoint的未读数量后 都刷新下@BindRedPoint
    Tree,//每个字段解析完后之后，设置@BindRedPoint的未读数量后， 最后统一刷新一下红点树

}