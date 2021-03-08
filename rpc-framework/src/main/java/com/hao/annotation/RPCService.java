package com.hao.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RPCService {

    /**
     * 服务版本
     */
    String version() default "";

    /**
     * 服务组
     */
    String group() default "";

}
