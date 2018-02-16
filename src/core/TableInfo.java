package core;

/**
 * 表示一张表中的注解信息，包括表名和表数据条件
 * This class contains table's name and columns
 *
 * @author Fish
 * 2018-2-16
 * */
final class TableInfo
{
    private String tableName = null;
    private String columns = null;

    public TableInfo()
    {}

    public TableInfo(String tableName, String columns)
    {
        this.tableName = tableName;
        this.columns = columns;
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getColumns()
    {
        return columns;
    }

    public void setColumns(String columns)
    {
        this.columns = columns;
    }

    @Override
    public String toString()
    {
        return "tableName: " + tableName + '\n' +
                "columns: " + columns;
    }
}
