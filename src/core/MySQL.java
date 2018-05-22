package core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 生成数据库语句的主要类<br>
 * generate the code of mysql<br>
 *
 * @author Fish
 * 2018-2-16
 * */
public final class MySQL
{
    // key 值为列名，value 值为实体类的 getter
    private static Map<String, String> beanInfo = null;

    // nobody gonna to instanse this class, you only can use the methods it let you use
    private MySQL()
    {}

    // to get table infomation and handle exceptions
    private static TableInfo getTableInfo(Object object)
    {
        try
        {
            TableInfo info = Parser.readAnnotation(object);
            if (info != null)
            {
                beanInfo = info.getBeanInfo();
            }

            return info;
        }
        catch (NoSuchMethodException e)
        {
            System.out.println("找不到方法！");
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            System.out.println("执行方法失败！");
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            System.out.println("访问异常！");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 生成查询语句，根据 object 来生成<br>
     * generate query code<br>
     *
     * @param object 给定的对象，根据这个对象的信息来查询，<br>
     *               如果想要查询整张表，传入一个空对象即可。<br>
     *               given object, how to query depends on this object,<br>
     *               if you want to query the whole table, just give a new Object.<br>
     * @return 生成的 MySQL 查询语句<br>
     *         the query code<br>
     * */
    public static String querySQL(Object object)
    {
        // get table infomation
        TableInfo info = getTableInfo(object);

        if (info != null)
        {
            return "SELECT * FROM " + info.getTableName() + " WHERE " + info.getColumns() + ";";
        }

        return null;
    }

    /**
     * 生成修改语句，根据 oldObject 来找到旧的数据，然后将数据修改成 newObject<br>
     * generate query code, it will change oldObject to newObject<br>
     *
     * @param oldObject 给定的对象，根据这个对象的信息来确定要被修改的数据。<br>
     *                  given object, which data will be updated depends on this object.<br>
     * @param newObject 要修改成啥数据就靠这个对象了。<br>
     *                  the new data you want to update.<br>
     * @return 生成的 MySQL 修改语句<br>
     *         the update code<br>
     */
    public static String updateSQL(Object oldObject, Object newObject)
    {
        // get table infomation
        TableInfo oldInfo = getTableInfo(oldObject);
        TableInfo newInfo = getTableInfo(newObject);

        if (oldInfo != null && newInfo != null)
        {
            // "1 = 1 and " is unnecessary
            String newColumns = newInfo.getColumns();
            if (newColumns.length() >= 10)
            {
                newColumns = newColumns.substring(10).replace(" and ", ", ");
            }

            return "UPDATE " + oldInfo.getTableName() + " SET "
                    + newColumns + " WHERE " + oldInfo.getColumns() + ";";
        }

        return null;
    }

    /**
     * 生成删除语句，根据 object 来生成<br>
     * generate delete code<br>
     *
     * @param object 给定的对象，根据这个对象的信息来确定要被删除的数据，<br>
     *               如果传入的对象没有具体信息，整个操作将被终止！<br>
     *               如果不终止，就会删除整张表！这后果很严重！<br>
     *               given object, which data will be deleted depends on this object,<br>
     *               if this object has not enough infomation to decide certain data,<br>
     *               this mission will be stop to prevent the whole table!<br>
     * @return 生成的 MySQL 删除语句<br>
     * the delete code<br>
     */
    public static String deleteSQL(Object object)
    {
        // get table infomation
        TableInfo info = getTableInfo(object);

        if (info != null)
        {
            // if WHERE 1 = 1, the whole table will be deleted!
            if ("1 = 1".equalsIgnoreCase(info.getColumns()))
            {
                System.out.println("您没有指定要删除的元素，删除整张表在这是不允许的！");
                System.out.println("You did not give a certain object! Delete the whole table is not allowed here!");
                return null;
            }
            return "DELETE FROM " + info.getTableName() + " WHERE " + info.getColumns() + ";";
        }

        return null;
    }

    /**
     * 生成插入语句，根据 object 来生成<br>
     * generate insert code<br>
     *
     * @param object 给定的对象，根据这个对象的信息来确定要被插入的数据。<br>
     *               given object, which data will be deleted depends on this object.<br>
     * @return 生成的 MySQL 删除语句，传入的对象至少要有一个属性不为 null，否则返回 null 。<br>
     *         the delete code, this object at least has one not-null value, or you only get a null.<br>
     */
    public static String insertSQL(Object object)
    {
        // get table information
        TableInfo info = getTableInfo(object);

        if (info != null)
        {
            String column = info.getColumns();
            if (column.length() >= 10)
            {
                column = column.substring(10);
                String[] columns = column.split("and");
                StringBuilder keys = new StringBuilder("(");
                StringBuilder values = new StringBuilder("(");
                for (String s : columns)
                {
                    String[] temp = s.split("=");
                    keys.append(temp[0].trim()).append(", ");
                    values.append(temp[1].trim()).append(", ");
                }
                keys.delete(keys.lastIndexOf(", "), keys.lastIndexOf(", ") + 2);
                keys.append(")");
                values.delete(values.lastIndexOf(", "), values.lastIndexOf(", ") + 2);
                values.append(")");

                return "INSERT INTO " + info.getTableName() + keys.toString() + " VALUES" + values.toString() + ";";
            }
            return null;
        }

        return null;
    }

    /**
     * 生成插入语句，根据 objects 来生成<br>
     * generate insert code
     *
     * @param objects 给定的对象，根据这个对象的信息来确定要被插入的数据。<br>
     *                你会得到类似于这样的 sql 语句：<br>
     *                INSERT INTO book(id, name) VALUES(1, '1美国v魁联盟看来'), (2, '2更开朗快乐'), (3, '3那个v慷慨');<br><br>
     *                given object, which data will be deleted depends on this object.<br>
     *                you will got:<br>
     *                INSERT INTO book(id, name) VALUES(1, '1美国v魁联盟看来'), (2, '2更开朗快乐'), (3, '3那个v慷慨');<br>
     * @return 生成的 MySQL 删除语句，传入的对象至少要有一个属性不为 null，否则返回 null 。<br>
     * the delete code, this object at least has one not-null value, or you only get a null.<br>
     */
    public static String insertSQLs(Object[] objects)
    {
        // get table information
        List<TableInfo> infos = new ArrayList<>();
        for (Object o : objects)
        {
            infos.add(getTableInfo(o));
        }

        String tableName = infos.get(0).getTableName();
        String column = infos.get(0).getColumns();
        if (infos.size() > 0)
        {
            // because you get '1 = 1 and name = "xxx" and price = 10', and '1 = 1 and ' is deprecated
            if (column.length() >= 10)
            {
                column = column.substring(10);
                String[] columns = column.split("and");
                StringBuilder keys = new StringBuilder("(");
                String[] temp = null;
                for (String s : columns)
                {
                    temp = s.split("=");
                    keys.append(temp[0].trim()).append(", ");
                }
                keys.delete(keys.lastIndexOf(", "), keys.lastIndexOf(", ") + 2);
                keys.append(")");

                StringBuilder values = new StringBuilder();
                StringBuilder value = null;
                for (TableInfo info : infos)
                {
                    column = info.getColumns().substring(10);
                    value = new StringBuilder("(");
                    columns = column.split("and");
                    for (String s : columns)
                    {
                        temp = s.split("=");
                        value.append(temp[1].trim()).append(", ");
                    }
                    value.delete(value.lastIndexOf(", "), value.lastIndexOf(", ") + 2);
                    value.append("), ");
                    values.append(value);
                }
                values.delete(values.lastIndexOf(", "), values.lastIndexOf(", ") + 2);

                return "INSERT INTO " + tableName + keys.toString() + " VALUES" + values.toString() + ";";
            }
            return null;
        }

        return null;
    }

    /**
     * 从 dataMap 中读取数据，并封装为指定对象返回<br>
     * read dataMap and return an instance of this data<br>
     *
     * @param <T> 对象类型，返回值依靠给定的 class 类型来决定<br>
     * @param dataMap 要被读取的 Map，包含表列名和数据，它会根据 key 值来调用相应的 setter 方法<br>
     *                it contains column and data, and invoke data's setter using key<br>
     * @param beanType 返回类型，如果是 Book 类对象，就传入 Book.class，<br>
     *                 因此，你不需要进行类型转换，内部已经转换好了。<br>
     *                 另外，由于内部会调用该类的无参构造器来初始化一个对象，会调用相应属性的 set 方法，<br>
     *                 所以这个 beanType 必须符合 javabean 标准，即拥有无参构造器和 setter 。<br>
     *                 return your data object, if it is Book class, then give it Book.class,<br>
     *                 therefore, you don't need to cast it.<br>
     *                 Also, this bean returned is created by none-argument constructor, and sets value by setter,<br>
     *                 you should give a class like javabean.<br>
     *
     * @return 返回封装好数据的 bean 对象<br>return object<br>
     */
    public static <T> T getBeanByMap(Map<String, Object> dataMap, Class<T> beanType)
    {
        Object object = null;
        try
        {
            // create an instance of return type
            object = beanType.newInstance();

            // init beanInfo
            getTableInfo(object);

            // take all things out from dataMap and box it in an object
            Set<String> columns = dataMap.keySet();
            Object value = null;
            Method setter = null;
            for (String column : columns)
            {
                value = dataMap.get(column);

                if (value != null)
                {
                    setter = beanType.getDeclaredMethod(beanInfo.get(column), value.getClass());
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
        return (T) object;
    }

    /**
     * 从 dataMaps 中读取数据，并封装为指定对象返回<br>
     * 这其实是遍历地去执行 getBeanByMap 方法，从而得到一个 bean 列表<br>
     * read dataMap and return an instance of this data<br>
     * this method actually invoke getBeanByMap to get so many beans<br>
     *
     * @param <T>      对象类型，返回值依靠给定的 class 类型来决定<br>
     * @param dataMaps  要被读取的 Map 列表，包含多个 Map 对象，每个都是表列名和数据，它会根据 key 值来调用相应的 setter 方法<br>
     *                 it contains many maps and everyone contains column and data, and invoke data's setter using key<br>
     * @param beanType 返回类型，如果是 Book 类对象，就传入 Book.class，<br>
     *                 因此，你不需要进行类型转换，内部已经转换好了。<br>
     *                 另外，由于内部会调用该类的无参构造器来初始化一个对象，会调用相应属性的 set 方法，<br>
     *                 所以这个 beanType 必须符合 javabean 标准，即拥有无参构造器和 setter 。<br>
     *                 return your data object, if it is Book class, then give it Book.class,<br>
     *                 therefore, you don't need to cast it.<br>
     *                 Also, this bean returned is created by none-argument constructor, and sets value by setter,<br>
     *                 you should give a class like javabean.<br>
     * @return 返回封装好数据的 bean 对象<br>return object
     */
    public static <T> List<T> getBeansByMaps(List<Map<String, Object>> dataMaps, Class<T> beanType)
    {
        List<T> beans = new ArrayList<>(dataMaps.size());

        // get beans by loop...
        for (Map<String, Object> data : dataMaps)
        {
            beans.add(getBeanByMap(data, beanType));
        }

        return beans;
    }
}
