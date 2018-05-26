# AutoMySQL
## MySql增删改查语句自动生成器 v1.0（MySQL code automatical generator v1.0）
--------------------------------------------------------------------------

### 下面演示了结合 EasyMySQL 小框架的查询操作：
#### if you use EasyMySQL and this jar:
#### EasyMySQL 请点击：[EasyMySQL](https://github.com/FishGoddess/EasyMySQL)
    // get information from file
    DBManager.update(new File("testFile/config/DB.properties"));

    // connect to database
    DBWorker.wakeUp();

    // query data
    List<Map> books = DBWorker.queryMaps("book");

    for (Map<String, Object> map : books)
    {
        // read data from map and box it an object
        Book book = MySQL.getBeanByMap(map, Book.class);
        System.out.println(book);
    }

    // release resources
    DBWorker.sleep();

<br/>

--------------------------------------------------------------------------

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! update !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!<br/>

### *2018-5-22:*
1. 新增了一个 String insertSQLs(Object[] objects) 方法，现在可以生成批量插入的语句了<br/>
(add a new method: String insertSQLs(Object[] objects), now you can generate a insert sql for batch!)<br/>
2. 大量优化了 javadoc，以前的 doc 挤在一起，丑的跟啥一样。。。现在排版比较整齐了。<br/>
(rewrite javadoc, now they are cuter :) ... )<br/>

### *2018-4-26:*
1. 新增通过 List<Map> 来得到多个 bean 对象的方法，这样就可以一次读取多个 Map了。<br/>
(add a new method which can get beans by one time...)<br/>

### *2018-4-25:*
1. 如果数据表和 bean 的名字一样，就可以不加 @Column 注解，这在类属性值很多时可以节省大量时间。<br/>
(If your table's column name is the same as the bean's name, you could pass the step of write @Column!)<br/>

2. 完善了 MySQL.getBeanByMap 方法，配合上一条改进来进行修正！<br/>
(update MySQL.getBeanByMap depends on the first update...)<br/>

3. 修复了 MySQL.getBeanByMap 方法的一个问题。<br/>
(fix a bug caused by MySQL.getBeanByMap...)<br/>

### *2018-3-11:*
新增了一个 ORM 方法，可以从 Map 对象中自动读取 key 和 value 来组装成一个对象，这个方法最主要是配合 EasyMySQL 小框架使用。上面有演示！<br/>

we prepare a new ORM function named getBeanByMap, and it is mainly used by EasyMySQL. You got a list of map from database, then you could change them into an object easily if you want.<br/>

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! update !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!<br/>

--------------------------------------------------------------------------

### 使用步骤：<br/>
    1. 在实体类上添加 Table 注解，值为数据表的名字
    2. 在字段上添加 Column 注解，值为对应的数据表列
    3. 给实体类添加 getter 和 setter（一个标准的 javabean 类）
    4. 执行相应的增删改查方法

    程序实例：
    Book.java
    TestUtil.java
***************************************************************************************************************
### using steps：<br/>
    1. add @Table on class declaration, the value is table name
    2. add @Column on field, the value is one column of table
    3. add getter and setter(a javabean class)
    4. do something

    examples：
    Book.java
    TestUtil.java

***************************************************************************************************************
    // 使用范例：                                                                                                      
    import core.MySQL;

    public class TestUtil
    {

        public static void main(String[] args) throws Exception
        {

            // 1. query
            {
                Book book = new Book();
                book.setName("python计算机视觉");
                //book.setPrice(59);

                // book2.name = null, book2.price = null
                Book book2 = new Book();

                System.out.println(MySQL.querySQL(book));
                System.out.println(MySQL.querySQL(book2));
            }

            // 2. update
            {
                Book book = new Book();
                book.setName("java框架一本通");

                Book book2 = new Book();
                book2.setName("C++大法好");
                book2.setPrice(99);

                System.out.println(MySQL.updateSQL(book, book2));
            }

            // 3. delete
            {
                // this is not allowed because it will delete the whole table!
                Book book = new Book();

                Book book2 = new Book();
                book2.setName("非卖品！");
                //book2.setPrice(99);

                System.out.println(MySQL.deleteSQL(book));
                System.out.println(MySQL.deleteSQL(book2));
            }

            // 4. insert
            {
                Book book = new Book();
                book.setName("java框架一本通");
                book.setPrice(65);

                Book book2 = new Book();
                book2.setName("全混声唱法教你上G5（非卖品）");

                System.out.println(MySQL.insertSQL(book));
                System.out.println(MySQL.insertSQL(book2));
            }
        }
    }
***************************************************************************************************************

