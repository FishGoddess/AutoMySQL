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