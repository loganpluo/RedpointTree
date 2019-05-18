package com.github.redpointtree.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by loganpluo on 2019/5/18.
 * ParseRedPointAnnotaionUtil 会解析 BindRedPoint,来刷新红点树
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface BindRedPoint {

    String redPointId() default "";
    String treeName() default "";

}
