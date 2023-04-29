package org.jingyuexing.annotation;

import org.jingyuexing.Types.MySQLType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对字段属性进行注解,可以设置字段属性的类型 在表中的名称,字段长度
 *
 * ```java
 *
 * @Column(type=MySQLType.VARCHAR,length=23,primary=MySQLType.NULL)
 *```
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column{
    MySQLType type() default  MySQLType.NULL;
    String name() default "";
    int length() default  0;
    String[] enums() default {};
    boolean autoIncrement() default false;
    boolean primaryKey() default false;
    boolean nullable() default true;
    String comment() default "";
    String primary() default "";
}