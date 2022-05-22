package jvmdog.core.sql.parse;

import java.lang.reflect.Field;
import java.util.List;

public class ObjectUpdate {
    private final TableDefinition table;
    private final List<ObjectParameter> parameters;
    private final ObjectCondition condition;
    
    public ObjectUpdate(TableDefinition table, List<ObjectParameter> parameters, ObjectCondition condition) {
        this.table = table;
        this.parameters = parameters;
        this.condition = condition;
    }
    public List<ObjectParameter> getParameters() {
        return parameters;
    }
    public TableDefinition getTable() {
        return table;
    }
    public ObjectCondition getCondition() {
        return condition;
    }
    
    public boolean applyUpdate(Object data){
        if(!Operators.apply(data, condition)){
            return false;
        }
        
        for(ObjectParameter objectParam: parameters){
            try {
                Field field = objectParam.getColumn().getField();
                field.setAccessible(true);
                Object val = convert(objectParam.getValue(), field.getType());
                field.set(data, val);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return true;
    }
    
    private static Object convert(Object val, Class<?> expectedClass){
        if(expectedClass == Integer.class || expectedClass == int.class){
            return ((Number)val).intValue();
        }
        return val;
    }
}
