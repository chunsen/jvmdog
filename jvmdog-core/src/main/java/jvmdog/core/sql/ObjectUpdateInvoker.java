package jvmdog.core.sql;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.linq4j.tree.Types;

import jvmdog.core.sql.parse.ObjectUpdate;

public class ObjectUpdateInvoker {
    
    public static final Method METHOD = Types.lookupMethod(ObjectUpdateInvoker.class, "execute", String.class, String.class, DataContext.class);

    public static Enumerable<?> execute(
            final String schemaName, final String tableName, final DataContext context) {
        System.out.println("update: tableName:" + tableName);
        return new AbstractEnumerable() {
            @Override
            public Enumerator enumerator() {
                int count = 0;
               
                ObjectUpdate objectUpdate = (ObjectUpdate)context.get("objectUpdate");
                final List rows = (List)context.get("rows");
                for(Object data: rows){
                    if(objectUpdate.applyUpdate(data)){
                        count++;
                    }
                }
                return Linq4j.singletonEnumerator(count);
            }
        };
    }
}
