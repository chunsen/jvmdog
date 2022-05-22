package jvmdog.core.sql.parse;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class Operators {
    private static final Map<String, BiFunction<List<?>, Object, Boolean>> operatorMap = new HashMap<>();
    
    public static boolean apply(Object data, ObjectCondition condition){
        BiFunction<List<?>, Object, Boolean> func = operatorMap.get(condition.getOperator().toLowerCase());
        if(func == null){
            throw new RuntimeException("Unknown operator: "+ condition.getOperator());
        }
        
        return func.apply(condition.getOperands(), data);
    }
    
    static {
        operatorMap.put("and", Operators::and);
        operatorMap.put("or", Operators::or);
        operatorMap.put("=", Operators::eq);
        operatorMap.put("!=", Operators::neq);
        operatorMap.put(">", Operators::gt);
    }
    
    private static Boolean and(List<?> params, Object data){
        ObjectCondition left = (ObjectCondition)params.get(0);
        ObjectCondition right = (ObjectCondition)params.get(1);
        return Operators.apply(data, left) && Operators.apply(data, right);
    }
    
    private static boolean or(List<?> params, Object data){
        ObjectCondition left = (ObjectCondition)params.get(0);
        ObjectCondition right = (ObjectCondition)params.get(1);
        return Operators.apply(data, left) || Operators.apply(data, right);
    }
    
    private static boolean eq(List<?> params, Object data){
        Object val = getFieldVal(params.get(0), data);
        Object expected = params.get(1);
        return val ==null ? false : val.equals(expected);
    }
    
    private static Object getFieldVal(Object name, Object data){
        if(name instanceof ColumnDefinition){
            Field field = ((ColumnDefinition)name).getField();
            field.setAccessible(true);
            try {
                Object val = field.get(data);
                return val;
            }catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
//        try {
//            Field[] fields = data.getClass().getDeclaredFields();
//            for(Field field: fields){
//                if(field.getName().equalsIgnoreCase(name)){
//                    field.setAccessible(true);
//                    Object val = field.get(data);
//                   return val;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }
    
    private static boolean neq(List<?> params, Object data){
        return !eq(params, data);
    }
    
    private static boolean gt(List<?> params, Object data){
        Number val = (Number)getFieldVal(params.get(0), data);
        Number expected = (Number)params.get(1);
        return val ==null? false: val.doubleValue()> expected.doubleValue();
    }
}
