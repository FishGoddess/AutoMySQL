package core;


import annotations.Column;
import annotations.Table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 使用反射从一个实体类中读取注解
 * Use reflect and get annotation in a class
 *
 * @author Fish
 * 2018-2-16
 * */
final class Parser
{
    // the constructor is private, you can only use the static methods
    private Parser()
    {}

    // change the first letter to upper
    // such as, attribute = "name", you will get a return like "getName"
    // it returns the name of getter
    private static String getter(String attribute)
    {
        return "get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
    }

    /**
     * 从 object 中读取指定的注解，Table 和 Column
     * read @Table and @Column from the given object
     *
     * @param object 要被读取注解的对象
     *               the given object
     * @return 返回读取到的注解信息，包括表名和列信息
     *         这里生成的列信息主要是用于 mySQL 中的 WHERE 语句，
     *         例如：表中列信息有 name 和 price，就会生成 “1 = 1 and name = "xxx" and price = 10”
     *         return infomation of annotation, including table name and column infomation,
     *         the column infomation is for WHERE in mySQL,
     *         for example, a table has two columns named "name" and "price",
     *         then you will get '1 = 1 and name = "xxx" and price = 10'
     * */
    public static TableInfo readAnnotation(Object object) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Class clazz = object.getClass(); // get class object

        // check if this object has any annotations named "Table"
        if (clazz.isAnnotationPresent(Table.class))
        {
            // get the value of table: table name
            String tableName = ((Table)clazz.getAnnotation(Table.class)).value();

            // check each field
            Field[] fields = clazz.getDeclaredFields();
            StringBuilder sb = new StringBuilder("1 = 1");
            for (Field f : fields)
            {
                // invoke getter of this field, and check if it is  not null
                Method get = clazz.getDeclaredMethod(getter(f.getName()));
                Object value = get.invoke(object);

                if (value != null)
                {
                    // check if this field has any annotations named "Column"
                    if (f.isAnnotationPresent(Column.class))
                    {
                        // get the value of field: column name
                        String columnName = ((Column)f.getAnnotation(Column.class)).value();

                        if (value instanceof String)
                        {
                            value = "\'" + value + "\'";
                        }

                        sb.append(" and ").append(columnName).append(" = ").append(value);
                    }
                }
            }

            return new TableInfo(tableName, sb.toString());
        }

        return null;
    }
}
