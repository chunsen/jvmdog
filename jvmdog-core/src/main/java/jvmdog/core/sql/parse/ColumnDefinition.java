package jvmdog.core.sql.parse;

import java.lang.reflect.Field;

public class ColumnDefinition {
    private final String name;
    private final Class<?> type;
    private final Field field;
    
    public ColumnDefinition(Field field) {
        this.name = field.getName();
        this.type = field.getType();
        this.field = field;
    }
    public String getName() {
        return name;
    }
    public Class<?> getType() {
        return type;
    }
    public Field getField() {
        return field;
    }
    
}
