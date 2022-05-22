package jvmdog.core.sql;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.calcite.linq4j.Enumerator;

public class ObjectEnumerator implements Enumerator<Object[]> {
    private final List<Object> items;
    private int index = -1;
    private Object[] current = null;
    
    protected ObjectEnumerator(List<Object> items){
        this.items = items;
    }

    @Override
    public Object[] current() {
        return current;
    }

    @Override
    public boolean moveNext() {
        index++;
        if(index>=items.size()){
            return false;
        }
        
        Object item = items.get(index);
        Field[] fields = item.getClass().getDeclaredFields();
        Object[] vals = new Object[fields.length];
        for(int i=0; i<fields.length; i++){
            Field field = fields[i];
            Object val = null;
            try {
                field.setAccessible(true);
                val = field.get(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
            vals[i] = val;
        }
        this.current = vals;
        return true;
    }

    @Override
    public void reset() {
        index = -1;
        current = null;
    }

    @Override
    public void close() {
        index = -1;
        current = null;
    }

}
