package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这表示数据库中的一张表，这是注解，添加到实体类上<br>
 * This class points to a table in mySql<br>
 *
 * @author Fish
 * 2018-2-16
 * */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table
{
    String value();
}
