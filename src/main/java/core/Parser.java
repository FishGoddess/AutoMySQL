package core;


import annotations.Column;
import annotations.Table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 使用反射从一个实体类中读取注解<br>
 * Use reflect and get annotation in a class<br>
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

    // change the first letter to upper
    // such as, attribute = "name", you will get a return like "setName"
    // it returns the name of setter
    private static String setter(String attribute)
    {
        return "set" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
    }

    /**
     * 从 object 中读取指定的注解，Table 和 Column<br>
     * read @Table and @Column from the given object<br>
     *
     * @param object 要被读取注解的对象<br>
     *               the given object<br>
     * @return 返回读取到的注解信息，包括表名和列信息<br>
     *         这里生成的列信息主要是用于 mySQL 中的 WHERE 语句，<br>
     *         例如：表中列信息有 name 和 price，就会生成 “1 = 1 and name = "xxx" and price = 10”<br>
     *         return infomation of annotation, including table name and column infomation,<br>
     *         the column infomation is for WHERE in mySQL,<br>
     *         for example, a table has two columns named "name" and "price",<br>
     *         then you will get '1 = 1 and name = "xxx" and price = 10'<br>
     * */
    static TableInfo readAnnotation(Object object) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
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
            Map<String, String> beanInfo = new HashMap<>();
            for (Field f : fields)
            {
                // invoke getter of this field, and check if it is  not null
                Method get = clazz.getDeclaredMethod(getter(f.getName()));
                Object value = get.invoke(object);

                // get the value of field: column name
                String columnName = null;

                // the column name will be the same as field name...
                if (f.getAnnotation(Column.class) == null)
                {
                    columnName = f.getName();
                }
                else // column name is given...
                {
                    columnName = ((Column) f.getAnnotation(Column.class)).value();
                }
                beanInfo.put(columnName, setter(f.getName()));

                if (value != null)
                {
                    // check if this field has any annotations named "Column"
                    //if (f.isAnnotationPresent(Column.class))
                    {
                        if (value instanceof String)
                        {
                            value = "\'" + value + "\'";
                        }

                        sb.append(" and ").append(columnName).append(" = ").append(value);
                    }
                }
            }

            return new TableInfo(tableName, sb.toString(), beanInfo);
        }

        return null;
    }

    /**
     * 从 dataMap 中读取数据，并封装为指定对象返回<br>
     * read dataMap and return an instance of this data<br>
     *
     * @param dataMap 要被读取的 Map，包含表列名和数据，它会根据 key 值来调用相应的 setter 方法<br>
     *                it contains column and data, and invoke data's setter using key<br>
     * @param returnType 返回类型，如果是 Book 类对象，就传入 Book.class，<br>
     *                   因此，你不需要进行类型转换，内部已经转换好了<br>
     *                   return your data object, if it is Book class, then give it Book.class,<br>
     *                   therefore, you don't need to cast it<br>
     *
     * @return 返回封装好数据的 bean 对象<br>return object
     * */
    static <T> T readMap(Map<String, Object> dataMap, Class<T> returnType)
    {
        Object object = null;
        try
        {
            // create an instance of return type
            object = returnType.newInstance();

            // take all things out from dataMap and box it in an object
            Set<String> columns = dataMap.keySet();
            Object value = null;
            Method setter = null;
            for (String column : columns)
            {
                value = dataMap.get(column);

                if (value != null)
                {
                    setter = returnType.getDeclaredMethod(setter(column), value.getClass());
                }

                // invoke the setter of object
                // only your table's column name equals to object's attribute name or setter,
                // can this method successfully invoke...
                if (setter != null)
                {
                    setter.invoke(object, value);
                }
            }
        }
        catch (InstantiationException e)
        {
            System.out.println("对象实例化失败！有可能是没有无参构造器导致的！");
        }
        catch (IllegalAccessException e)
        {
            System.out.println("无法访问对象！");
        }
        catch (NoSuchMethodException e)
        {
            System.out.println("没有相应的 setter 方法！请生成属性的 setter！");
        }
        catch (InvocationTargetException e)
        {
            System.out.println("方法执行出错！请检查权限！");
        }

        // return the object, and you don't need to cast it by yourself
        return (T)object;
    }
}
