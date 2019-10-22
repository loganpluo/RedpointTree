package com.github.redpointtree.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by loganpluo on 2019/5/18.
 * ParseRedPointAnnotaionUtil 会解析 RedPointCountRsp,来刷新红点树
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RedPointCountRsp {

    //可以定义刷新类型
    InvalidateType invalidateType() default InvalidateType.Point;
    String treeName() default "";

}
