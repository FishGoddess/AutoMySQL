package core;

import java.util.Map;

/**
 * 表示一张表中的注解信息，包括表名和表数据条件<br>
 * This class contains table's name and columns<br>
 *
 * @author Fish
 * 2018-2-16
 * */
final class TableInfo
{
    private String tableName = null;
    private String columns = null;

    // key 值为列名，value 值为实体类的 getter
    private Map<String, String> beanInfo = null;

    public TableInfo()
    {}

    public TableInfo(String tableName, String columns, Map<String, String> beanInfo)
    {
        this.tableName = tableName;
        this.columns = columns;
        this.beanInfo = beanInfo;
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

    public Map<String, String> getBeanInfo()
    {
        return beanInfo;
    }

    public void setBeanInfo(Map<String, String> beanInfo)
    {
        this.beanInfo = beanInfo;
    }

    @Override
    public String toString()
    {
        return "tableName: " + tableName + '\n' +
                "columns: " + columns;
    }
}
