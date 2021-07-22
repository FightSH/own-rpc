package com.hao.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RPCReference {

    /**
     * 服务版本
     */
    String version() default "";

    /**
     * 服务组
     */
    String group() default "";

    /**
     * 代理方式  jdk  or  cglib
     * @return
     */
    String proxyType() default "jdk";

}
