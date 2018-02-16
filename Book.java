import annotations.Column;
import annotations.Table;

// test class
@Table("book")
public class Book
{
    @Column("name")
    private String name = null;

    @Column("price")
    private Integer price = null;

    public Book()
    {
    }

    public Book(String name, Integer price)
    {
        this.name = name;
        this.price = price;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getPrice()
    {
        return price;
    }

    public void setPrice(Integer price)
    {
        this.price = price;
    }
}

