package org.dromara.omind.mp.token.annotation;

import java.lang.annotation.*;

/**
 * @author : flainsky
 * @version : V0.0
 * @data : 2024/8/1 19:13
 * @company : ucode
 * @email : 298542443@qq.com
 * @title :
 * @Description :
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TokenCheck {

    /**
     * 是否检查签名
     * @return
     */
    boolean isSign() default true;

}
