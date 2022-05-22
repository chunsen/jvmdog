package jvmdog.core.sql.parse;

public class ObjectParameter {
    private final ColumnDefinition column;
    private final Object value;
    
    public ObjectParameter(ColumnDefinition column, Object value) {
        this.column = column;
        this.value = value;
    }
    public ColumnDefinition getColumn() {
        return column;
    }
    public Object getValue() {
        return value;
    }
    
}
