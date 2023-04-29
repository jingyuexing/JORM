package org.jingyuexing.annotation;
import org.jingyuexing.Types.EncodeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table{
    String name() default "";
    String prefix() default "";

    EncodeType encode() default EncodeType.UTF8;

    String engines() default "InnoDB";
}