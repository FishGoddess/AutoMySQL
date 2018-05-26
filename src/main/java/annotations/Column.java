package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这表示数据库中一张表的一列，也就是表中的字段名<br>
 * This is a column in a table<br>
 *
 * @author Fish
 * 2018-2-16
 * */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column
{
    String value();
}
