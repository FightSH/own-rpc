package com.hao.annotation;

import com.hao.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Import(CustomScannerRegistrar.class)
public @interface RPCScan {

    String[] basePackage();

}
