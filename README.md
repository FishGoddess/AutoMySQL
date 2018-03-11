# AutoMySQL
## MySql增删改查语句自动生成器 v1.0（MySQL code automatical generator v1.0）
--------------------------------------------------------------------------

### 下面演示了结合 easyMySQL 小框架的查询操作：
#### if you use AutoMySQL and this jar:
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

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! update !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!<br/>

### *2018-3-11:*
新增了一个 ORM 方法，可以从 Map 对象中自动读取 key 和 value 来组装成一个对象，这个方法最主要是配合 EasyMySQL 小框架使用。上面有演示！<br/>

we prepare a new ORM function named getBeanByMap, and it is mainly used by EasyMySQL. You got a list of map from database, then you could change them into an object easily if you want.<br/>


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
