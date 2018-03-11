package core;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 生成数据库语句的主要类
 * generate the code of mysql
 *
 * @author Fish
 * 2018-2-16
 * */
public final class MySQL
{
    // nobody gonna to instanse this class, you only can use the methods it let you use
    private MySQL()
    {}

    // to get table infomation and handle exceptions
    private static TableInfo getTableInfo(Object object)
    {
        try
        {
            return Parser.readAnnotation(object);
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
     * 生成查询语句，根据 object 来生成
     * generate query code
     *
     * @param object 给定的对象，根据这个对象的信息来查询，
     *               如果想要查询整张表，传入一个空对象即可。
     *               given object, how to query depends on this object,
     *               if you want to query the whole table, just give a new Object.
     * @return 生成的 MySQL 查询语句
     *         the query code
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
     * 生成修改语句，根据 oldObject 来找到旧的数据，然后将数据修改成 newObject
     * generate query code, it will change oldObject to newObject
     *
     * @param oldObject 给定的对象，根据这个对象的信息来确定要被修改的数据。
     *                  given object, which data will be updated depends on this object.
     * @param newObject 要修改成啥数据就靠这个对象了。
     *                  the new data you want to update.
     * @return 生成的 MySQL 修改语句
     *         the update code
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
     * 生成删除语句，根据 object 来生成
     * generate delete code
     *
     * @param object 给定的对象，根据这个对象的信息来确定要被删除的数据，
     *               如果传入的对象没有具体信息，整个操作将被终止！
     *               如果不终止，就会删除整张表！这后果很严重！
     *               given object, which data will be deleted depends on this object,
     *               if this object has not enough infomation to decide certain data,
     *               this mission will be stop to prevent the whole table!
     * @return 生成的 MySQL 删除语句
     * the delete code
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
     * 生成插入语句，根据 object 来生成
     * generate insert code
     *
     * @param object 给定的对象，根据这个对象的信息来确定要被插入的数据。
     *               given object, which data will be deleted depends on this object.
     * @return 生成的 MySQL 删除语句，传入的对象至少要有一个属性不为 null，否则返回 null 。
     *         the delete code, this object at least has one not-null value, or you only get a null.
     */
    public static String insertSQL(Object object)
    {
        // get table infomation
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

                return "INSERT " + info.getTableName() + keys.toString() + " VALUES" + values.toString() + ";";
            }
            return null;
        }

        return null;
    }

    /**
     * 从 dataMap 中读取数据，并封装为指定对象返回
     * read dataMap and return an instance of this data
     *
     * @param dataMap 要被读取的 Map，包含表列名和数据，它会根据 key 值来调用相应的 setter 方法
     *                it contains column and data, and invoke data's setter using key
     * @param beanType 返回类型，如果是 Book 类对象，就传入 Book.class，
     *                 因此，你不需要进行类型转换，内部已经转换好了。
     *                 另外，由于内部会调用该类的无参构造器来初始化一个对象，会调用相应属性的 set 方法，
     *                 所以这个 beanType 必须符合 javabean 标准，即拥有无参构造器和 setter 。
     *                 return your data object, if it is Book class, then give it Book.class,
     *                 therefore, you don't need to cast it.
     *                 Also, this bean returned is created by none-argument constructor, and sets value by setter,
     *                 you should give a class like javabean.
     *
     * @return 返回封装好数据的 bean 对象
     */
    public static <T> T getBeanByMap(Map<String, Object> dataMap, Class<T> beanType)
    {
        return Parser.readMap(dataMap, beanType);
    }
}
