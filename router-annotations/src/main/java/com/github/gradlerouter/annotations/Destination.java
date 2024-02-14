package com.github.gradlerouter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明当前注解可以修饰的原始，此处表示可以用在类上面
 */
@Target({ElementType.TYPE})
/**
 * 说明当前注解可以被保留的时间：
 * source只会在.java文件中保留，编译成.class文件则不存在
 * class在编译成.class文件时会存在
 * runtime在运行时仍会保留
 */
@Retention(RetentionPolicy.CLASS)
public @interface Destination {

    /**
     *
     * 如果是String URL() default "";则可为空
     * @return 当前页面的URL，不能为空
     */
    String url();

    /**
     * 例如“个人主页”
     * @return 对应当前页面的中文描述
     */
    String description();

}
