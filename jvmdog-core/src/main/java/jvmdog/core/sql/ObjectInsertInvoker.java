package jvmdog.core.sql;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.linq4j.tree.Types;

public class ObjectInsertInvoker {
    
    public static final Method METHOD = Types.lookupMethod(ObjectInsertInvoker.class, "execute", String.class, String.class, DataContext.class);

    public static Enumerable<?> execute(
            final String schemaName, final String tableName, final DataContext context) {
        final List rows = (List)context.get("rows");
        
        System.out.println("insert: tableName:" + tableName);
        return new AbstractEnumerable() {
            @Override
            public Enumerator enumerator() {
                int count = 1;
                //TODO:
//                DataObject dataObj = new DataObject();
//                dataObj.setName("insert");
//                dataObj.setAge(31);
//                rows.add(dataObj);
                
                return Linq4j.singletonEnumerator(count);
            }
        };
    }
}
