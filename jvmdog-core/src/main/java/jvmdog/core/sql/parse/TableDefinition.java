package jvmdog.core.sql.parse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TableDefinition {
    private final String name;
    private final List<ColumnDefinition> columns;
    private final Class<?> tableType;

    public TableDefinition(Class<?> tableType) {
        this.tableType = tableType;
        
        this.name = tableType.getName().replace('.', '_');
        this.columns = new ArrayList<>();
        Field[] fields = tableType.getDeclaredFields();
        for(Field field: fields){
            columns.add(new ColumnDefinition(field));
        }
    }

    public Class<?> getTableType() {
        return tableType;
    }

    public ColumnDefinition getColumn(int index){
        return this.columns.get(index);
    }
    
    public ColumnDefinition getColumn(String name){
        for(ColumnDefinition column: this.columns){
            if(column.getName().equalsIgnoreCase(name)){
                return column;
            }
        }
        return null;
    }
    
    public int getColumnCount(){
        return this.columns.size();
    }
    
    public String getName() {
        return name;
    }

    public List<ColumnDefinition> getColumns() {
        return columns;
    }

}
